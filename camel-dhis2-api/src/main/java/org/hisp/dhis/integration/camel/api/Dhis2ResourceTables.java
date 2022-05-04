/*
 * Copyright (c) 2004-2022, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.integration.camel.api;

import org.hisp.dhis.api.v2_37_4.model.ImportOptions;
import org.hisp.dhis.api.v2_37_4.model.Notification;
import org.hisp.dhis.api.v2_37_4.model.WebMessage;
import org.hisp.dhis.integration.sdk.Dhis2Client;
import org.hisp.dhis.integration.sdk.api.operation.PutOperation;

public class Dhis2ResourceTables
{
    private final Dhis2Client dhis2Client;

    public Dhis2ResourceTables( Dhis2Client dhis2Client )
    {
        this.dhis2Client = dhis2Client;
    }

    public void analytics( Boolean skipAggregate, Boolean skipEvents, Integer lastYears )
    {
        PutOperation putOperation = dhis2Client.put( "resourceTables/analytics" );
        if ( skipEvents != null )
        {
            putOperation.withParameter( "skipEvents", String.valueOf( skipEvents ) );
        }
        if ( skipEvents != null )
        {
            putOperation.withParameter( "skipAggregate", String.valueOf( skipAggregate ) );
        }
        if ( lastYears != null )
        {
            putOperation.withParameter( "lastYears", String.valueOf( lastYears ) );
        }

        String taskId = putOperation.transfer().returnAs( WebMessage.class ).getResponse().get().get( "id" );

        Notification notification = null;
        while ( notification == null || !notification.getCompleted().get() )
        {
            try
            {
                Thread.sleep( 30000 );
            }
            catch ( InterruptedException e )
            {
                throw new RuntimeException( e );
            }
            Iterable<Notification> notifications = dhis2Client.get( "system/tasks/ANALYTICS_TABLE/{taskId}",
                taskId ).withoutPaging().transfer().returnAs( Notification.class );
            if ( notifications.iterator().hasNext() )
            {
                notification = notifications.iterator().next();
                if ( notification.getLevel().get().equals( ImportOptions.NotificationLevel.ERROR ) )
                {
                    throw new RuntimeException( "Analytics failed => " + notification );
                }
            }
        }
    }

}
