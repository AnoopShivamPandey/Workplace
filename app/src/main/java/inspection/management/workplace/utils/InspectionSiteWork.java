package inspection.management.workplace.utils;

public class InspectionSiteWork {
    private String status,name;
    private String inspection_id;
    private String project,topic_id;
    private String employee_name,start_date,end_date,topic,location,is_delete,question_color;

    public InspectionSiteWork(String status, String inspection_id, String project, String employee_name, String start_date, String end_date, String topic, String location, String is_delete, String topic_id,String name,String question_color) {
        this.status = status;
        this.inspection_id = inspection_id;
        this.project = project;
        this.employee_name = employee_name;
        this.start_date = start_date;
        this.end_date = end_date;
        this.topic = topic;
        this.location = location;
        this.is_delete = is_delete;
        this.topic_id = topic_id;
        this.name = name;
        this.question_color=question_color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getInspection_id() {
        return inspection_id;
    }

    public void setInspection_id(String inspection_id) {
        this.inspection_id = inspection_id;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public void setQuestion_color(String question_color) {
        this.employee_name = question_color;
    }
    public String getQuestion_color() {
        return question_color;
    }


}
