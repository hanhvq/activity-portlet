$(function() {

  // Like/Unlike
  $(".activity-container").each(function(e) {
    var activityId = $(this).find(".activity-data").attr("data-activityid");
    
    $(this).jzFind(".like-container").on("click", ".like_" + activityId, function() {
	    $(this).closest(".like-container").jzLoad("Controller.doLike()", {
	      id : activityId
	    }, function() {
	    });
	  });
	  
  });

  // Add comment
	$(".activity-container").each(function(e) {
    var activityId = $(this).find(".activity-data").attr("data-activityid");
    
	  $(this).find(".comment-container").on("click", ".comment_" + activityId, function(e) {
	    var content = $(this).jzFind(".comment-content").val();
	    
	    $(this).closest(".comment-container").jzLoad("Controller.doComment()", {
	      content: content,
	      activityId : activityId
	    }, function() {
	    });
	    
	    e.preventDefault();
	    
	  });
  });
  
  // Delete comment
  $(".activity-container").each(function(e) {
    var activityId = $(this).find(".activity-data").attr("data-activityid");
    
      $(this).find(".comment-container").on("click", ".delete-comment_" + activityId, function(e) {
        var commentId = $(e.target).data('commentid');
        var content = $(this).jzFind(".comment-content").val();
        
        $(this).closest(".comment-container").jzLoad("Controller.deleteComment()", {
  	      activityId : activityId,
	        commentId: commentId
	      }, function() {
	      });

        $(e.target).parent().parent().remove();
        e.preventDefault();
      });
  });


  // Load initial likes
  $(".activity-container").each(function() {
    var activityId = $(this).find(".activity-data").attr("data-activityid");
    
    $(this).find(".like-container").jzLoad("Controller.loadLike()", {
      id : activityId
    }, function() {
    });
  });


  // Load initial comments
  $(".activity-container").each(function() {
    var activityId = $(this).find(".activity-data").attr("data-activityid");
    
    $(this).find(".comment-container").jzLoad("Controller.loadComment()", {
      id : activityId
    }, function() {
    });
  });
  
});