/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package exo.social.portlet.providers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import juzu.Path;
import juzu.SessionScoped;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.web.application.RequestContext;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 13, 2012  
 */
public class DataProvider {

  @Inject
  ActivityManager am;
  
  @Inject
  IdentityManager identityManager;
  
  public List<ExoSocialActivity> getActivities() throws Exception {
    String remoteUser = RequestContext.getCurrentInstance().getRemoteUser();
    Identity currentUser = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, remoteUser, true);
    ListAccess<ExoSocialActivity> activitiesListAccess = am.getActivitiesWithListAccess(currentUser);
    return new ArrayList<ExoSocialActivity>(Arrays.asList(activitiesListAccess.load(0, 100)));
  }
  
  static List<String> types = new ArrayList<String>();
  static {
    types.add("LINK");
    types.add("DEFAULT");
    types.add("DOC");
  }
  
  public List<String> getTypes() {
    return types;
  }
}
