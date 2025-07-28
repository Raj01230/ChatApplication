package com.dollop.app.utils;

public final class NotificationConstant {

	private NotificationConstant() {

	}

	public static final String EVENT_CREATED_MESSAGE = "A new event has been created.";
	public static final String EVENT_CREATED_URL = "/event/details"; // Frontend route

	public static final String EVENT_APPROVED_MESSAGE = "Your event has been approved.";
	public static final String EVENT_APPROVED_URL = "/event/approved";

	public static final String EVENT_REJECTED_MESSAGE = "Your event has been rejected.";
	public static final String EVENT_REJECTED_URL = "/event/rejected";

	public static final String EVENT_CANCELLED_MESSAGE = "An event has been cancelled.";
	public static final String EVENT_CANCELLED_URL = "/event/cancelled";

	public static final String USER_JOINED_EVENT_MESSAGE = "A user has joined your event.";
	public static final String USER_JOINED_EVENT_URL = "/event/participants";

	public static final String USER_LEFT_EVENT_MESSAGE = "A user has left your event.";
	public static final String USER_LEFT_EVENT_URL = "/event/participants";

	public static final String USER_REMOVED_FROM_EVENT_MESSAGE = "You have been removed from an event.";
	public static final String USER_REMOVED_FROM_EVENT_URL = "/my-events";

	public static final String USER_REQUESTED_TO_JOIN_EVENT_MESSAGE = "A user has requested to join your event.";
	public static final String USER_REQUESTED_TO_JOIN_EVENT_URL = "/event/requests";

	public static final String GROUP_CREATED_MESSAGE = "A new group has been created.";
	public static final String GROUP_CREATED_URL = "/group/details";

	public static final String GROUP_APPROVAL_MESSAGE = "A new group has been created,Give permission";
	public static final String GROUP_APPROVAL_URL = "/group/approved";

	public static final String GROUP_APPROVED_MESSAGE = "Your group has been approved by Admin.";
	public static final String GROUP_APPROVED_URL = "/group/approved";

	public static final String GROUP_REJECTED_MESSAGE = "Your group has been rejected.";
	public static final String GROUP_REJECTED_URL = "/group/rejected";

	public static final String GROUP_UPDATED_MESSAGE = "Group details have been updated.";
	public static final String GROUP_UPDATED_URL = "/group/details";

	public static final String GROUP_JOIN_MESSAGE = "Admin has joined you in this Group.";
	public static final String GROUP_JOIN_URL = "/group/requests";

	public static final String USER_JOINED_GROUP_MESSAGE = "A user has joined your group.";
	public static final String USER_JOINED_GROUP_URL = "/group/members";

	public static final String USER_LEFT_GROUP_MESSAGE = "A user has left your group.";
	public static final String USER_LEFT_GROUP_URL = "/group/members";

	public static final String GENERAL_MESSAGE = "You have a new notification.";
	public static final String GENERAL_URL = "/dashboard";
}
