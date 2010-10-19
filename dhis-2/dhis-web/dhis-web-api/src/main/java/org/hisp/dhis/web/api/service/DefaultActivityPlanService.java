/**
 * 
 */
package org.hisp.dhis.web.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.activityplan.Activity;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.web.api.model.ActivityPlan;
import org.hisp.dhis.web.api.model.Beneficiary;
import org.hisp.dhis.web.api.service.mapping.ActivitiesMapper;
import org.hisp.dhis.web.api.service.mapping.TaskMapper;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author abyotag_adm
 *
 */
public class DefaultActivityPlanService implements IActivityPlanService {

	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	@Autowired
	private org.hisp.dhis.activityplan.ActivityPlanService activityPlanService;
	
	@Autowired
        private PatientAttributeValueService patientAttValueService ;
	
	@Autowired
    private CurrentUserService currentUserService;
	
	// -------------------------------------------------------------------------
    // MobileDataSetService
    // -------------------------------------------------------------------------	
	
	public ActivityPlan getCurrentActivityPlan(String localeString) 
	{
		Collection<OrganisationUnit> units = currentUserService.getCurrentUser().getOrganisationUnits();
        OrganisationUnit unit = null;
        
        if( units.size() > 0 )
        {
        	unit = units.iterator().next();       	
        }
        else
        {
        	return null;
        }		
		
		DateTime dt = new DateTime();
        DateMidnight from = dt.withDayOfMonth( 1 ).toDateMidnight();
        DateMidnight to = from.plusMonths( 1 );

        Collection<Activity> allActivities = activityPlanService.getActivitiesByProvider( unit );
        Collection<Activity> activities = new ArrayList<Activity>();
        for ( Activity activity : allActivities )
        {
            long dueTime = activity.getDueDate().getTime();
            if ( to.isBefore( dueTime ) )
            {
                continue;
            }
            
            if (from.isBefore( dueTime ) || !activity.getTask().isCompleted()) {
                activities.add( activity );
            }
        }

//        ActivityPlan plan = new ActivitiesMapper().getModel( activities );
        ActivityPlan plan = getActivityPlanModel(activities);
        

        return plan;
	}
	
	
	
	
	//method replace the Mappers
        private org.hisp.dhis.web.api.model.ActivityPlan getActivityPlanModel( Collection<org.hisp.dhis.activityplan.Activity> activities )
            {
                ActivityPlan plan = new ActivityPlan();

                if ( activities == null || activities.isEmpty() )
                {
                    return plan;
                }

                List<org.hisp.dhis.web.api.model.Activity> items = new ArrayList<org.hisp.dhis.web.api.model.Activity>();
                plan.setActivitiesList( items );
                int i = 0;
                for ( org.hisp.dhis.activityplan.Activity activity : activities )
                {
                    //there are error on db with patientattributeid 14, so I limit the patient to be downloaded
                    if(i<=10){
                        org.hisp.dhis.web.api.model.Activity temp = getActivityModel( activity);
                        if(temp != null){
                            items.add(temp);
                        }
                        i++;
                    }
                }
                return plan;
            }
        
        
        private org.hisp.dhis.web.api.model.Activity getActivityModel( org.hisp.dhis.activityplan.Activity activity )
        {
            if ( activity == null )
            {
                return null;
            }
            org.hisp.dhis.web.api.model.Activity item = new org.hisp.dhis.web.api.model.Activity();
            Patient patient = activity.getBeneficiary();
            
            item.setBeneficiary( getBeneficiaryModel(patient) );
            item.setDueDate( activity.getDueDate() );
            item.setTask( new TaskMapper().getModel( activity.getTask()) );
            return item;
        }
        
        
        
        private org.hisp.dhis.web.api.model.Beneficiary getBeneficiaryModel( Patient patient )
        {
            

            Beneficiary beneficiary = new Beneficiary();

            Set<String> patientAttValues = new HashSet<String>();
            
            
            beneficiary.setId( patient.getId() );
            beneficiary.setFirstName( patient.getFirstName() );
            beneficiary.setLastName( patient.getLastName() );
            beneficiary.setMiddleName( patient.getMiddleName() );
            
            for(PatientAttributeValue value : patientAttValueService.getPatientAttributeValues( patient )){
                patientAttValues.add( value.getPatientAttribute().getName() +" : "+ value.getValue());
            }
            beneficiary.setPatientAttValues( patientAttValues );
            
            return beneficiary;
        }
}
