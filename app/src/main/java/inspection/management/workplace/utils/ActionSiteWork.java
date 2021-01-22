package inspection.management.workplace.utils;

public class ActionSiteWork {
    private String status;
    private String action_id;
    private String action,topic_id;
    private String employee_name,start_date,end_date,topic,location,is_delete,topic_name;

    public ActionSiteWork(String status, String action_id, String action, String employee_name, String start_date, String end_date, String topic, String location, String is_delete,String topic_id, String topic_name) {
        this.status = status;
        this.action_id = action_id;
        this.action = action;
        this.employee_name = employee_name;
        this.start_date = start_date;
        this.end_date = end_date;
        this.topic = topic;
        this.location = location;
        this.is_delete = is_delete;
        this.topic_id= topic_id;
        this.topic_name= topic_name;
    }

    public String getTopic_name() {
        return topic_name;
    }

    public void setTopic_name(String topic_name) {
        this.topic_name = topic_name;
    }

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(String is_delete) {
        this.is_delete = is_delete;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction_id() {
        return action_id;
    }

    public void setAction_id(String action_id) {
        this.action_id = action_id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }
}
