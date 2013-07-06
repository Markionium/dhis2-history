package org.hisp.dhis.mapgeneration;

/*
 * Copyright (c) 2011, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

import java.awt.Color;

import org.hisp.dhis.organisationunit.OrganisationUnit;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Geometry;

/**
 * An internal representation of a map object in a map layer.
 * 
 * It encapsulates all the information of an atomic object on a map, i.e. its
 * name, value, fill color, fill opacity, stroke color, stroke width, and
 * potentially its radius should it be represented as a point.
 * 
 * It may be the associated with an interval of an interval set and should be
 * associated with a map layer.
 * 
 * Finally, one should extend this class with an implementation that uses a
 * specific platform, e.g. GeoTools to draw the map.
 * 
 * @author Olai Solheim <olais@ifi.uio.no>
 */
public class InternalMapObject
{
    protected String name;

    protected double value;

    protected int radius;

    protected Color fillColor;

    protected float fillOpacity;

    protected Color strokeColor;

    protected int strokeWidth;

    protected InternalMapLayer mapLayer;

    protected Interval interval;
    
    private Geometry geometry;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public InternalMapObject()
    {
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    /**
     * Builds the GeoTools geometric primitive for a given organisation unit and
     * sets it for this map object.
     * 
     * Quick guide to how geometry is stored in DHIS:
     * 
     * Geometry for org units is stored in the DB as [[[[0.32, -33.87], [23.99,
     * -43.02], ...]]], and may be retrieved by calling the getCoordinates
     * method of OrganisationUnit.
     * 
     * The coordinates vary according to feature type, which can be found with a
     * call to getFeatureType of OrganisationUnit. It varies between the
     * following structures (names are omitted in the actual coordinates
     * string):
     * 
     * multipolygon = [ polygon0 = [ shell0 = [ point0 = [0.32, -33.87], point1
     * = [23.99, -43.02], point2 = [...]], hole0 = [...], hole1 = [...]],
     * polygon1 = [...] polygon2 = [...]] polygon = [ shell0 = [ point0 = [0.32,
     * -33.87], point1 = [23.99, -43.02]], hole0 = [...], hole1 = [...]]
     * 
     * point = [0.32, -33.87]
     * 
     * Multi-polygons are stored as an array of polygons. Polygons are stored as
     * an array of linear-rings, where the first linear-ring is the shell, and
     * remaining linear-rings are the holes in the polygon. Linear-rings are
     * stored as an array of points, which in turn is stored as an array of
     * (two) components as a floating point type.
     * 
     * There are three types of geometry that may be stored in a DHIS org unit:
     * point, polygon, and multi-polygon. This method supports all three.
     * 
     * NOTE However, as of writing, there is a bug in DHIS OrganisationUnit
     * where when getFeatureType reports type Polygon, getCoordinates really
     * returns coordinates in the format of type MultiPolygon.
     * 
     * @param orgUnit the organisation unit
     */
    public static Geometry buildAndApplyGeometryForOrganisationUnit( OrganisationUnit orgUnit )
    {
        // The final GeoTools primitive
        Geometry primitive = null;

        // The DHIS coordinates as string
        String coords = orgUnit.getCoordinates();

        // The json root that is parsed from the coordinate string
        JsonNode root = null;

        try
        {
            // Create a parser for the json and parse it into root
            JsonParser parser = new ObjectMapper().getJsonFactory().createJsonParser( coords );
            root = parser.readValueAsTree();
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( ex );
        }

        // Use the factory to build the correct type based on the feature type
        // Polygon is treated similarly as MultiPolygon        
        if ( OrganisationUnit.FEATURETYPE_POINT.equals( orgUnit.getFeatureType() ) )
        {
            primitive = GeoToolsPrimitiveFromJsonFactory.createPointFromJson( root );
        }
        else if ( OrganisationUnit.FEATURETYPE_POLYGON.equals( orgUnit.getFeatureType() ) )
        {
            primitive = GeoToolsPrimitiveFromJsonFactory.createMultiPolygonFromJson( root ); 
        }
        else if ( OrganisationUnit.FEATURETYPE_MULTIPOLYGON.equals( orgUnit.getFeatureType() ) )
        {
            primitive = GeoToolsPrimitiveFromJsonFactory.createMultiPolygonFromJson( root );
        }
        else
        {
            throw new RuntimeException( "Not sure what to do with the feature type '" + orgUnit.getFeatureType() + "'" );
        }

        return primitive;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    /**
     * Gets the name of this map object.
     * 
     * @return the name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Sets the name of this map object.
     * 
     * @param name the name
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * Gets the value for this map object.
     * 
     * @return the value
     */
    public double getValue()
    {
        return this.value;
    }

    /**
     * Sets the value for this map object.
     * 
     * @param value the value
     */
    public void setValue( double value )
    {
        this.value = value;
    }

    /**
     * Gets the radius for this map object (if point).
     * 
     * @return the radius
     */
    public int getRadius()
    {
        return this.radius;
    }

    /**
     * Sets the radius for this map object (if point).
     * 
     * @param radius the fill color
     */
    public void setRadius( int radius )
    {
        this.radius = radius;
    }

    /**
     * Gets the fill color for this map object.
     * 
     * @return the fill color
     */
    public Color getFillColor()
    {
        return this.fillColor;
    }

    /**
     * Sets the fill color for this map object.
     * 
     * @param fillColor the fill color
     */
    public void setFillColor( Color fillColor )
    {
        this.fillColor = fillColor;
    }

    /**
     * Gets the fill opacity for this object.
     * 
     * @return the fill opacity
     */
    public float getFillOpacity()
    {
        return this.fillOpacity;
    }

    /**
     * Sets the fill opacity for this object.
     * 
     * @param fillOpacity the fill opacity
     */
    public void setFillOpacity( float fillOpacity )
    {
        this.fillOpacity = fillOpacity;
    }

    /**
     * Gets the stroke color for this map object.
     * 
     * @return the stroke color
     */
    public Color getStrokeColor()
    {
        return this.strokeColor;
    }

    /**
     * Sets the stroke color for this map object.
     * 
     * @param strokeColor the stroke color
     */
    public void setStrokeColor( Color strokeColor )
    {
        this.strokeColor = strokeColor;
    }

    /**
     * Gets the stroke width for this map object.
     * 
     * @return the stroke width
     */
    public int getStrokeWidth()
    {
        return this.strokeWidth;
    }

    /**
     * Sets the stroke width for this map object.
     * 
     * @param strokeWidth
     */
    public void setStrokeWidth( int strokeWidth )
    {
        this.strokeWidth = strokeWidth;
    }

    /**
     * Gets the map layer this map object is associated with.
     * 
     * @return the map layer
     */
    public InternalMapLayer getMapLayer()
    {
        return this.mapLayer;
    }

    /**
     * Sets the map layer this object is associated with.
     * 
     * @param mapLayer the map layer
     */
    public void setMapLayer( InternalMapLayer mapLayer )
    {
        this.mapLayer = mapLayer;
    }

    /**
     * Gets the interval this map object is associated with.
     * 
     * @return the interval
     */
    public Interval getInterval()
    {
        return this.interval;
    }

    /**
     * Sets the interval this map object is associated with and updates this map
     * object with the properties (e.g. fill color) from the given interval.
     * 
     * @param interval the interval
     */
    public void setInterval( Interval interval )
    {
        this.interval = interval;
        this.fillColor = interval.getColor();
    }

    /**
     * Gets the geometry for this map object which is any of the GeoTools
     * primitives.
     * 
     * @return the GeoTools geometric primitive
     */
    public Geometry getGeometry()
    {
        return this.geometry;
    }

    /**
     * Sets the geometry for this map object which is any of the GeoTools
     * primitives.
     * 
     * @param geometry the GeoTools geometric primitive
     */
    public void setGeometry( Geometry geometry )
    {
        this.geometry = geometry;
    }

    /**
     * Returns a string representing this object, e.g. "InternalMapObject {
     * name: "Khambia", value: 34.22, radius: 1.00, fillColor:
     * java.awt.Color(255, 255, 255), fillOpacity: 0.75, strokeColor:
     * java.awt.Color(0, 0, 0), strokeWidth: 2 }".
     */
    public String toString()
    {
        return String.format( "InternalMapObject {" + " name: \"%s\"," + " value: %.2f," + " radius: %d,"
            + " fillColor: %s," + " fillOpacity: %.2f" + " strokeColor: %s," + " strokeWidth: %d" + " }", name, value,
            radius, fillColor, fillOpacity, strokeColor, strokeWidth );
    }
}
