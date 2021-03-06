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

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.integration.sdk.api.Dhis2Client;
import org.hisp.dhis.integration.sdk.api.IterableDhis2Response;
import org.hisp.dhis.integration.sdk.api.operation.GetOperation;

/**
 * Sample API used by Dhis2 Component whose method signatures are read from Java
 * source.
 */
public class Dhis2Get
{
    private final Dhis2Client dhis2Client;

    public Dhis2Get( Dhis2Client dhis2Client )
    {
        this.dhis2Client = dhis2Client;
    }

    public InputStream resource( String path, String fields, String filter, Map<String, Object> queryParams )
    {
        GetOperation getOperation = dhis2Client.get( path );
        if ( fields != null )
        {
            getOperation.withFields( fields );
        }

        if ( filter != null )
        {
            getOperation.withFilter( filter );
        }

        if ( queryParams != null )
        {
            for ( Map.Entry<String, Object> queryParam : queryParams.entrySet() )
            {
                if ( queryParam.getValue() instanceof List )
                {
                    for ( String queryValue : (List<String>) queryParam.getValue() )
                    {
                        getOperation.withParameter( queryParam.getKey(), queryValue );
                    }
                }
                else
                {
                    getOperation.withParameter( queryParam.getKey(), (String) queryParam.getValue() );
                }

            }
        }

        return getOperation.withParameter( "paging", "false" ).transfer().read();
    }

    public <T> Iterator<T> collection( String path, String itemType, Boolean paging, String fields, String filter )
    {
        GetOperation getOperation = dhis2Client.get( path );
        if ( fields != null )
        {
            getOperation.withFields( fields );
        }

        if ( filter != null )
        {
            getOperation.withFilter( filter );
        }
        Iterable<T> iterable;

        IterableDhis2Response iteratorDhis2Response;
        if ( paging == null || paging )
        {
            iteratorDhis2Response = getOperation.withPaging().transfer();
        }
        else
        {
            iteratorDhis2Response = getOperation.withoutPaging().transfer();
        }

        if ( itemType == null )
        {
            iterable = (Iterable<T>) iteratorDhis2Response
                .returnAs( Map.class, path );
        }
        else
        {
            try
            {
                iterable = (Iterable<T>) iteratorDhis2Response
                    .returnAs( Class.forName( itemType ), path );
            }
            catch ( ClassNotFoundException e )
            {
                throw new RuntimeException( e );
            }
        }

        return iterable.iterator();
    }

}
