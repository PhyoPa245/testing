/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
package org.lamsfoundation.lams.usermanagement;

import java.io.IOException;
import java.util.Date;
import org.lamsfoundation.lams.AbstractLamsTestCase;
import org.lamsfoundation.lams.usermanagement.dao.hibernate.AuthenticationMethodDAO;
import org.lamsfoundation.lams.usermanagement.dao.hibernate.OrganisationDAO;
import org.lamsfoundation.lams.usermanagement.dao.hibernate.OrganisationTypeDAO;
import org.lamsfoundation.lams.usermanagement.dao.hibernate.RoleDAO;
import org.lamsfoundation.lams.usermanagement.dao.hibernate.UserDAO;
import org.lamsfoundation.lams.usermanagement.dao.hibernate.UserOrganisationRoleDAO;
import org.lamsfoundation.lams.usermanagement.service.UserManagementService;

/**
 * @author Manpreet Minhas
 */
public class TestUserManagementService extends AbstractLamsTestCase {
	
	protected UserDAO userDAO;
	protected RoleDAO roleDAO;
	protected OrganisationDAO organisationDAO;
	protected OrganisationTypeDAO organisationTypeDAO;
	protected UserManagementService userManagementService;
	protected AuthenticationMethodDAO authenticationMethodDAO;
	protected UserOrganisationRoleDAO userOrganisationRoleDAO;
	
	public TestUserManagementService(String name){
		super(name);
	}
	protected void setUp()throws Exception{
		super.setUp();
		userDAO =(UserDAO)context.getBean("userDAO");
		organisationDAO =(OrganisationDAO)context.getBean("organisationDAO");
		organisationTypeDAO =(OrganisationTypeDAO)context.getBean("organisationTypeDAO");
		userManagementService = (UserManagementService)context.getBean("userManagementService");
		authenticationMethodDAO =(AuthenticationMethodDAO)context.getBean("authenticationMethodDAO");
		roleDAO = (RoleDAO)context.getBean("roleDAO");		
		userOrganisationRoleDAO = (UserOrganisationRoleDAO)context.getBean("userOrganisationRoleDAO");
	}

	/**
	 * (non-Javadoc)
	 * @see org.lamsfoundation.lams.AbstractLamsTestCase#getContextConfigLocation()
	 */
	protected String[] getContextConfigLocation() {
		return new String[] {"org/lamsfoundation/lams/applicationContext.xml"};
	}
	public void testSaveOrganisation(){
		Organisation organisation = new Organisation("Test Organisation",
													 "Test Organisation Description",													
													 new Date(),													 
													 organisationTypeDAO.getOrganisationTypeById(new Integer(1)));
		
		Integer organisationID = userManagementService.saveOrganisation(organisation,new Integer(1));		
		assertNotNull(organisationID);
	}
	public void testSaveUser(){
		User user = new User();
		user.setLogin("Monu");
		user.setPassword("Monu");
		user.setDisabledFlag(new Boolean(false));
		user.setCreateDate(new Date());
		user.setAuthenticationMethod(authenticationMethodDAO.getAuthenticationMethodById(new Integer(2)));
		user.setBaseOrganisation(organisationDAO.getOrganisationById(new Integer(1)));		
		assertNotNull(userManagementService.saveUser(user, new Integer(3)));		
	}	
	public void testGetOrganisationsForUserByRole()throws IOException{
		String packet = userManagementService.getWDDXForOrganisationsForUserByRole(new Integer(4),"AUTHOR");
		System.out.println(packet);
	}
	public void testGetUsersFromOrganisationByRole() throws IOException{
		String packet = userManagementService.getUsersFromOrganisationByRole(new Integer(4),"AUTHOR");
		System.out.println(packet);
	}
	/**
	 * (non-Javadoc)
	 * @see org.lamsfoundation.lams.AbstractLamsTestCase#getHibernateSessionFactoryName()
	 */
	protected String getHibernateSessionFactoryName() {		
		return "coreSessionFactory";
	}
}
