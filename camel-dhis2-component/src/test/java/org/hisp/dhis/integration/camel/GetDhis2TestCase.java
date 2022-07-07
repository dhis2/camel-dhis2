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
package org.hisp.dhis.integration.camel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.hisp.dhis.api.model.v2_38_1.OrganisationUnit;
import org.junit.jupiter.api.Test;

public class GetDhis2TestCase extends AbstractDhis2TestSupport
{
    @Test
    public void testCollectionEndpoint()
    {
        Iterator<OrganisationUnit> organisationUnitIterator = requestBody( "direct://collection", null );
        List<OrganisationUnit> organisationUnits = new ArrayList<>();
        organisationUnitIterator.forEachRemaining( organisationUnits::add );

        assertTrue( organisationUnits.size() >= 1 );
    }

    @Test
    public void testResourceEndpoint()
    {
        OrganisationUnit organisationUnit = requestBodyAndHeaders( "direct://resource", null,
            Map.of( "orgUnitId", Environment.ORG_UNIT_ID ) );
        assertEquals( Environment.ORG_UNIT_ID, organisationUnit.getId().get() );
    }

    @Override
    protected RouteBuilder createRouteBuilder()
    {
        return new RouteBuilder()
        {
            public void configure()
            {
                from( "direct://collection" )
                    .to(
                        "dhis2://get/collection?path=organisationUnits&paging=true&itemType=org.hisp.dhis.api.model.v2_38_1.OrganisationUnit" );

                from( "direct://resource" )
                    .toD( "dhis2://get/resource?path=organisationUnits/${header.orgUnitId}" )
                    .unmarshal().json( OrganisationUnit.class );
            }
        };
    }
}
