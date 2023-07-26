package cdax.naindex.datalib.data;

/**
 * author Created by harrishuang on 12/7/21.
 * email : huangjinping1000@163.com
 */
public class ContactInfo {
    public long id = 0L;
    public String name;
    public String number;
    public boolean isChecked = false;
    public String type;
    public Long lastUpdateTime = 0L;
    public Long lastContactTime = 0L;
    public Long lastUsedTime = 0L;
    public String contactTimes;

    public ContactInfo() {
    }
}
