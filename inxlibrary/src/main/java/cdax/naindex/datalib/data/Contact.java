package cdax.naindex.datalib.data;


public class Contact {
    private String contact_display_name;
    private String number;
    private String up_time;
    private String last_time_contacted;
    private String times_contacted;


    public String getContact_display_name() {
        return contact_display_name;
    }

    public void setContact_display_name(String contact_display_name) {
        this.contact_display_name = contact_display_name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUp_time() {
        return up_time;
    }

    public void setUp_time(String up_time) {
        this.up_time = up_time;
    }

    public String getLast_time_contacted() {
        return last_time_contacted;
    }

    public void setLast_time_contacted(String last_time_contacted) {
        this.last_time_contacted = last_time_contacted;
    }

    public String getTimes_contacted() {
        return times_contacted;
    }

    public void setTimes_contacted(String times_contacted) {
        this.times_contacted = times_contacted;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "contact_display_name='" + contact_display_name + '\'' +
                ", number='" + number + '\'' +
                ", up_time='" + up_time + '\'' +
                ", last_time_contacted='" + last_time_contacted + '\'' +
                ", times_contacted='" + times_contacted + '\'' +
                '}';
    }
}
