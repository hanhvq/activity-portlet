package exo.social.portlet;

import exo.social.portlet.qualifiers.Current;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.service.LinkProvider;
import org.exoplatform.web.application.RequestContext;

import juzu.Action;
import juzu.Path;
import juzu.Resource;
import juzu.Response;
import juzu.SessionScoped;
import juzu.View;
import juzu.plugin.ajax.Ajax;
import juzu.template.Template;
import exo.social.portlet.models.Activity;
import exo.social.portlet.providers.DataProvider;
import exo.social.portlet.templates.main;
import exo.social.portlet.templates.comments;
import exo.social.portlet.templates.index;
import exo.social.portlet.templates.item;
import exo.social.portlet.templates.likes;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/** @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a> */
public class Controller
{

  @Inject ActivityManager am;
  @Inject IdentityManager im;
  @Inject DataProvider    dp;
  
  @Inject @Path("main.gtmpl")     main main;
  @Inject @Path("form.gtmpl")     Template form;
  @Inject @Path("index.gtmpl")    index index;
  @Inject @Path("item.gtmpl")     item item;
  @Inject @Path("likes.gtmpl")    likes likes;
  @Inject @Path("comments.gtmpl") comments comments;

  @Inject @SessionScoped Identity currentUser;

  @View
  public void index() throws IOException, Exception
  {
//    form.render();
    main
        .with()
        .activities(dp.getActivities())
        .render();
  }

  @View
  public void activity(String activityId) throws IOException
  {
    
//    index
//        .with()
//        .activityId(activityId)
//        .render();

  }
  
  @Ajax
  @Resource
  public void doPost(String content) throws Exception {
    ExoSocialActivity activity = new ExoSocialActivityImpl(currentUser.getId(), null, content);

    am.saveActivity(currentUser, activity);
    
    item.with().activity(activity).postIdentity(currentUser).render();

//    main
//        .with()
//        .activities(dp.getActivities())
//        .render();
  }
  
  @Ajax
  @Resource
  public void doLike(String id) {
    ExoSocialActivity activity = am.getActivity(id);
    List<Identity> likers = likers(activity);
    boolean isLiking = likers.contains(currentUser);

    // remove like
    if (isLiking) {
      List<String> likes = new ArrayList<String>(Arrays.asList(activity.getLikeIdentityIds()));
      likes.remove(currentUser.getId());
      likers.remove(currentUser);
      activity.setLikeIdentityIds(likes.toArray(new String[]{}));
      am.updateActivity(activity);
    }
    // add like
    else {
      List<String> likes = new ArrayList<String>(Arrays.asList(activity.getLikeIdentityIds()));
      likes.add(currentUser.getId());
      likers.add(currentUser);
      activity.setLikeIdentityIds(likes.toArray(new String[]{}));
      am.updateActivity(activity);
    }

    likes
        .with()
        .likers(likers)
        .isLiking(!isLiking)
        .activityid(id)
        .render();
  }

  @Ajax
  @Resource
  public void loadLike(String id) {
    ExoSocialActivity activity = am.getActivity(id);
    List<Identity> likers = likers(activity);
    boolean isLiking = likers.contains(currentUser);

    likes
        .with()
        .likers(likers)
        .isLiking(isLiking)
        .activityid(id)
        .render();
  }

  @Ajax
  @Resource
  public void doComment(String content, String activityId) {
    //
    ExoSocialActivity activity = am.getActivity(activityId);
    ExoSocialActivity comment = new ExoSocialActivityImpl(currentUser.getId(), null, content);

    //
    am.saveComment(activity, comment);

    //
    List<Activity> comments = comments(activity);

    this.comments
        .with()
        .comments(comments)
        .activityid(activityId)
        .render();
    
  }

  @Ajax
  @Resource
  public void loadComment(String id) {

    ExoSocialActivity activity = am.getActivity(id);
    List<Activity> comments = comments(activity);
    
    this.comments
        .with()
        .comments(comments)
        .activityid(id)
        .render();
  }

  @Ajax
  @Resource
  public void deleteComment(String activityId, String commentId) {
    //
    ExoSocialActivity activity = am.getActivity(activityId);

    //
    am.deleteComment(activityId, commentId);

    //
    List<Activity> comments = comments(activity);

    this.comments
        .with()
        .comments(comments)
        .activityid(activityId)
        .render();
  }

  @Action
  public Response loadActivity(String activityId) {
    return Controller_.activity(activityId);
  }

  @Action
  public Response back() {
    return Controller_.index();
  }

  private List<Identity> likers(ExoSocialActivity activity) {

    List<Identity> likers = new ArrayList<Identity>();
    for (String likerId : activity.getLikeIdentityIds()) {
      Identity liker = im.getIdentity(likerId, true);
      applyDefaultAvatar(liker);
      likers.add(liker);
    }

    return likers;

  }

  private List<Activity> comments(ExoSocialActivity activity) {
    List<Activity> comments = new ArrayList<Activity>();
    for (ExoSocialActivity data : am.getCommentsWithListAccess(activity).loadAsList(0, 10)) {
      Identity poster = im.getIdentity(data.getUserId(), true);
      applyDefaultAvatar(poster);
      comments.add(new Activity(poster, data, poster.getId().equals(currentUser.getId()), getPostedTimeInSpaceString(data.getPostedTime())));
    }

    return comments;

  }

  // TODO : remove this crap when social will be able to do it smartly
  public static void applyDefaultAvatar(Identity i) {
    
    if (i.getProfile().getAvatarUrl() == null) {
      i.getProfile().setAvatarUrl(LinkProvider.PROFILE_DEFAULT_AVATAR_URL);
    }

  }

  private String getPostedTimeInSpaceString(long postedTime) {
    long time = (new Date().getTime() - postedTime) / 1000;
    long value;
    if (time < 60) {
      return "less than a minute ago";
    } else {
      if (time < 120) {
        return "about a minute ago";
      } else {
        if (time < 3600) {
          value = Math.round(time / 60);
          return "about "+ value + " minutes ago";
        } else {
          if (time < 7200) {
            return "about an hour ago";
          } else {
            if (time < 86400) {
              value = Math.round(time / 3600);
              return "about " + value + " hours ago";
            } else {
              if (time < 172800) {
                return "about a day ago";
              } else {
                if (time < 2592000) {
                  value = Math.round(time / 86400);
                  return "about " + value + " days ago";
                } else {
                  if (time < 5184000) {
                    return "about a month ago";
                  } else {
                    value = Math.round(time / 2592000);
                    return "about " + value + " months ago";
                  }
                }
              }
            }
          }
        }
      }
    }
  }


}
