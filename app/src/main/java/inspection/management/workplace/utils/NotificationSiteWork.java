package inspection.management.workplace.utils;

public class NotificationSiteWork {
    private String status;
    private String notification_id;
    private String notification;
    private String sent_date;

    public NotificationSiteWork (String notification_id, String notification, String sent_date) {
        this.notification_id = notification_id;
        this.notification = notification;
        this.sent_date = sent_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getSent_date() {
        return sent_date;
    }

    public void setSent_date(String sent_date) {
        this.sent_date = sent_date;
    }
}
