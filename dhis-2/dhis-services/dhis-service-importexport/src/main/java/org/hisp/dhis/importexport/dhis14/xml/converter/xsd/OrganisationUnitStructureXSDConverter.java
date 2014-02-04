package org.hisp.dhis.importexport.dhis14.xml.converter.xsd;
/*
 * Copyright (c) 2004-2013, University of Oslo
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



import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportParams;

/**
 * @author Jason P. Pickering
 * @version $Id: OrganisationUnitXSDConverter.java 6455 2011-02-17 09:04:37Z jasonp $
  */


public class OrganisationUnitStructureXSDConverter
extends AbstractXSDConverter
{


    public OrganisationUnitStructureXSDConverter()
    {
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        //if ( params.getOrganisationUnits() != null && params.getOrganisationUnits().size() > 0 )
        //{
            writer.openElement( "xsd:element", "name", "OrgUnitStructure" );

            writeAnnotation( writer );

            writer.openElement( "xsd:complexType" );

            writer.openElement( "xsd:sequence" );

            writeLongInteger( writer, "OrgUnitStructureID", 1, true );

            writeText( writer, "OrgUnitStructureName", 1, true, 230 );
            
            writeMemo( writer, "OrgUnitStructureDescription", 0, false, 536870910 );

            writeInteger( writer, "OrgUnitLevelCount", 0, false );

            writer.closeElement();

            writer.closeElement();

            writer.closeElement();
        //}
    }

    public void read( XMLReader reader, ImportParams params )
    {
        // Not implemented
    }

}
