package org.hisp.dhis.vn.chr.object.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.jdbc.FormManager;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class DeleteObjectAction extends ActionSupport{
	
	// -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

	private FormManager formManager;
	
	private FormService formService;
	
	// -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

	// Form ID
	private Integer formId;
	
	// Object ID
	private Integer id;
	
	// message
	private String message;
	
	// -----------------------------------------------------------------------------------------------
    // Getter && Setter
    // -----------------------------------------------------------------------------------------------

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public void setFormId(Integer formId) {
		this.formId = formId;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getFormId() {
		return this.formId;
	}
	
	public void setFormManager(FormManager formManager) {
		this.formManager = formManager;
	}

	public void setFormService(FormService formService) {
		this.formService = formService;
	}
	
	// -----------------------------------------------------------------------------------------------
    // Implement : process Select SQL 
    // -----------------------------------------------------------------------------------------------

	public String execute() throws Exception {
		
		try {

			Form form = formService.getForm(formId.intValue());
			
			formManager.deleteObject(form, id.intValue());
			
			message = i18n.getString("delete") + " " + i18n.getString("success");
			
			return SUCCESS;

		} catch (Exception ex) {
			ex.printStackTrace();
			
			message = i18n.getString("delete") + " " + i18n.getString("error");
			message += "<br>"+i18n.getString("delete_message_error");
		}

		return ERROR;
	}
}
