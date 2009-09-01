package org.hisp.dhis.vn.chr.object.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.ElementService;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.jdbc.FormManager;

import com.opensymphony.xwork2.Action;

public class ListRelativeObjectAction
    implements Action
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

    private FormManager formManager;

    private FormService formService;

    private ElementService elementService;

    private SystemSettingManager systemSettingManager;

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private Integer formId;

    private String objectId;

    private Form form;

    private ArrayList<Object> data;

    private Collection<Element> formLinks;

    private String column;

    private ArrayList<String> parentObject;

    // -----------------------------------------------------------------------------------------------
    // Getter && Setter
    // -----------------------------------------------------------------------------------------------
    
    public ArrayList<String> getParentObject()
    {
        return parentObject;
    }

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    public Integer getFormId()
    {
        return formId;
    }

    public void setFormId( Integer formId )
    {
        this.formId = formId;
    }

    public void setFormManager( FormManager formManager )
    {
        this.formManager = formManager;
    }

    public void setFormService( FormService formService )
    {
        this.formService = formService;
    }

    public ArrayList<Object> getData()
    {
        return data;
    }

    public Form getForm()
    {
        return form;
    }

    public void setForm( Form form )
    {
        this.form = form;
    }

    public void setElementService( ElementService elementService )
    {
        this.elementService = elementService;
    }

    public Collection<Element> getFormLinks()
    {
        return formLinks;
    }

    public void setFormLinks( Collection<Element> formLinks )
    {
        this.formLinks = formLinks;
    }

    public String getObjectId()
    {
        return objectId;
    }

    public void setObjectId( String objectId )
    {
        this.objectId = objectId;
    }

    public String getColumn()
    {
        return column;
    }

    public void setColumn( String column )
    {
        this.column = column;
    }

    // -----------------------------------------------------------------------------------------------
    // Implement : process Select SQL
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        form = formService.getForm( formId.intValue() );

        formLinks = elementService.getElementsByFormLink( form );

        int numberOfRecords = Integer.parseInt( (String) systemSettingManager.getSystemSetting( SystemSettingManager.KEY_CHR_NUMBER_OF_RECORDS ) );
        
        data = formManager.listRelativeObject( form, column, objectId, numberOfRecords );

        if ( objectId != null )
        {

            for ( Element element : form.getElements() )
            {

                if ( element.getFormLink() != null )
                {

                    Form fparent = element.getFormLink();

                    ArrayList<String> data = formManager.getObject( fparent, Integer.parseInt( objectId ) );

                    parentObject = new ArrayList<String>();

                    int k = 0;

                    for ( Egroup egroup : fparent.getEgroups() )
                    {
                        for ( Element e : egroup.getElements() )
                        {

                            if ( data.get( k ) != null )
                                parentObject.add( e.getLabel() + " : " + data.get( k ) );
                            k++;
                            if ( k == fparent.getNoColumnLink() )
                                break;
                        }// end for element

                        if ( k == fparent.getNoColumnLink() )
                            break;
                    }// end for egroup
                }
            }
        }

        return SUCCESS;
    }
}
