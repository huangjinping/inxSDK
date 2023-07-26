package cdax.naindex;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class ContactAccessorCopy {


    private final String TAG = "ContactAccessor";
    private Context mContext;

    public ContactAccessorCopy(Context mContext) {
        this.mContext = mContext;
    }

    public JSONArray getContracts() {
        int limit = 2147483647;

        Cursor idCursor = mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{"raw_contact_id"}, null, null, "raw_contact_id ASC");
        ArrayList endContactIds = new ArrayList();

        while (idCursor.moveToNext()) {
            String raw_id = idCursor.getString(idCursor.getColumnIndex("raw_contact_id"));
            endContactIds.add(raw_id);
        }
        Log.d(TAG, TAG + new Gson().toJson(endContactIds));


        idCursor.close();
        if (endContactIds.size() == 0) {
            return new JSONArray();
        } else {
            WhereOptions idOptions = this.buildIdClause(endContactIds);
            Cursor c = mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI, (String[]) null, idOptions.getSelection(), idOptions.getSelectionArgs(), "raw_contact_id ASC");
            HashMap<String, Boolean> populate = this.buildPopulationSet(new JSONArray());
            JSONArray contacts = this.populateContactArray(limit, populate, c);
            return contacts;
        }

    }


    private JSONArray populateContactArray(int limit, HashMap<String, Boolean> populate, Cursor c) {
        long rawId = 0L;
        long oldRawId = 0L;
        boolean newContact = true;
        String mimetype = "";
        JSONArray contacts = new JSONArray();
        JSONObject contact = new JSONObject();
        JSONArray organizations = new JSONArray();
        JSONArray addresses = new JSONArray();
        JSONArray phones = new JSONArray();
        JSONArray emails = new JSONArray();
        JSONArray ims = new JSONArray();
        JSONArray urls = new JSONArray();
        JSONArray photos = new JSONArray();
        if (c.getCount() > 0) {
            while (true) {
                if (!c.moveToNext() || contacts.length() > limit - 1) {
                    if (contacts.length() < limit) {
                        contacts.put(this.populateContact(populate, contact, organizations, addresses, phones, emails, ims, urls, photos));
                    }
                    break;
                }

                try {
                    rawId = c.getLong(c.getColumnIndex("raw_contact_id"));
                    if (c.getPosition() == 0) {
                        oldRawId = rawId;
                    }

                    if (oldRawId != rawId) {
                        contacts.put(this.populateContact(populate, contact, organizations, addresses, phones, emails, ims, urls, photos));
                        contact = new JSONObject();
                        organizations = new JSONArray();
                        addresses = new JSONArray();
                        phones = new JSONArray();
                        emails = new JSONArray();
                        ims = new JSONArray();
                        urls = new JSONArray();
                        photos = new JSONArray();
                        newContact = true;
                    }

                    if (newContact) {
                        newContact = false;
                        contact.put("id", rawId);
                    }

                    for (String columnNames:c.getColumnNames()) {
                        Log.d(TAG, columnNames+"======"+c.getString(c.getColumnIndex(columnNames)));

                    }


//                    Log.d(TAG, new Gson().toJson( c.getColumnNames()));



//                    contact.put("contact_last_updated_timestamp", c.getString(c.getColumnIndex("contact_last_updated_timestamp")));
//                    contact.put("last_time_contacted", c.getString(c.getColumnIndex("last_time_contacted"))+"");
//                    contact.put("times_contacted", c.getString(c.getColumnIndex("times_contacted")));

                    mimetype = c.getString(c.getColumnIndex("mimetype"));
                    if (mimetype.equals("vnd.android.cursor.item/name")) {
                        contact.put("displayName", c.getString(c.getColumnIndex("data1")));
                    }

                    if (mimetype.equals("vnd.android.cursor.item/name") && this.isRequired("name", populate)) {
                        contact.put("name", this.nameQuery(c));
                    } else if (mimetype.equals("vnd.android.cursor.item/phone_v2") && this.isRequired("phoneNumbers", populate)) {
                        phones.put(this.phoneQuery(c));
                    } else if (mimetype.equals("vnd.android.cursor.item/email_v2") && this.isRequired("emails", populate)) {
                        emails.put(this.emailQuery(c));
                    } else if (mimetype.equals("vnd.android.cursor.item/postal-address_v2") && this.isRequired("addresses", populate)) {
                        addresses.put(this.addressQuery(c));
                    } else if (mimetype.equals("vnd.android.cursor.item/organization") && this.isRequired("organizations", populate)) {
                        organizations.put(this.organizationQuery(c));
                    } else if (mimetype.equals("vnd.android.cursor.item/im") && this.isRequired("ims", populate)) {
                        ims.put(this.imQuery(c));
                    } else if (mimetype.equals("vnd.android.cursor.item/note") && this.isRequired("note", populate)) {
                        contact.put("note", c.getString(c.getColumnIndex("data1")));
                    } else if (mimetype.equals("vnd.android.cursor.item/nickname") && this.isRequired("nickname", populate)) {
                        contact.put("nickname", c.getString(c.getColumnIndex("data1")));
                    } else if (mimetype.equals("vnd.android.cursor.item/website") && this.isRequired("urls", populate)) {
                        urls.put(this.urlQuery(c));
                    } else if (mimetype.equals("vnd.android.cursor.item/contact_event")) {
                        if (3 == c.getInt(c.getColumnIndex("data2")) && this.isRequired("birthday", populate)) {
                            contact.put("birthday", c.getString(c.getColumnIndex("data1")));
                        }
                    } else if (mimetype.equals("vnd.android.cursor.item/photo") && this.isRequired("photos", populate)) {
                        long contactId = c.getLong(c.getColumnIndex("contact_id"));
                        photos.put(this.photoQuery(c, contactId));
                    }
                } catch (JSONException var21) {
                    Log.e("ContactsAccessor", var21.getMessage(), var21);
                }

                oldRawId = rawId;
            }
        }

        c.close();
        return contacts;
    }


    private JSONObject nameQuery(Cursor cursor) {
        JSONObject contactName = new JSONObject();

        try {
            String familyName = cursor.getString(cursor.getColumnIndex("data3"));
            String givenName = cursor.getString(cursor.getColumnIndex("data2"));
            String middleName = cursor.getString(cursor.getColumnIndex("data5"));
            String honorificPrefix = cursor.getString(cursor.getColumnIndex("data4"));
            String honorificSuffix = cursor.getString(cursor.getColumnIndex("data6"));
            StringBuffer formatted = new StringBuffer("");
            if (honorificPrefix != null) {
                formatted.append(honorificPrefix + " ");
            }

            if (givenName != null) {
                formatted.append(givenName + " ");
            }

            if (middleName != null) {
                formatted.append(middleName + " ");
            }

            if (familyName != null) {
                formatted.append(familyName + " ");
            }

            if (honorificSuffix != null) {
                formatted.append(honorificSuffix + " ");
            }

            contactName.put("familyName", familyName);
            contactName.put("givenName", givenName);
            contactName.put("middleName", middleName);
            contactName.put("honorificPrefix", honorificPrefix);
            contactName.put("honorificSuffix", honorificSuffix);
            contactName.put("formatted", formatted);
        } catch (JSONException var9) {
            Log.e("ContactsAccessor", var9.getMessage(), var9);
        }

        return contactName;
    }


    private JSONObject phoneQuery(Cursor cursor) {
        JSONObject phoneNumber = new JSONObject();

        try {
            phoneNumber.put("id", cursor.getString(cursor.getColumnIndex("_id")));
            phoneNumber.put("pref", false);
            phoneNumber.put("value", cursor.getString(cursor.getColumnIndex("data1")));
            phoneNumber.put("type", this.getPhoneType(cursor.getInt(cursor.getColumnIndex("data2")) + ""));
        } catch (JSONException var4) {
            Log.e("ContactsAccessor", var4.getMessage(), var4);
        } catch (Exception var5) {
            Log.e("ContactsAccessor", var5.getMessage(), var5);
        }

        return phoneNumber;
    }

    private JSONObject emailQuery(Cursor cursor) {
        JSONObject email = new JSONObject();

        try {
            email.put("id", cursor.getString(cursor.getColumnIndex("_id")));
            email.put("pref", false);
            email.put("value", cursor.getString(cursor.getColumnIndex("data1")));
            email.put("type", this.getContactType(cursor.getInt(cursor.getColumnIndex("data2"))));
        } catch (JSONException var4) {
            Log.e("ContactsAccessor", var4.getMessage(), var4);
        }

        return email;
    }

    private JSONObject imQuery(Cursor cursor) {
        JSONObject im = new JSONObject();

        try {
            im.put("id", cursor.getString(cursor.getColumnIndex("_id")));
            im.put("pref", false);
            im.put("value", cursor.getString(cursor.getColumnIndex("data1")));
            im.put("type", this.getContactType(cursor.getInt(cursor.getColumnIndex("data2"))));
        } catch (JSONException var4) {
            Log.e("ContactsAccessor", var4.getMessage(), var4);
        }

        return im;
    }

    private JSONObject urlQuery(Cursor cursor) {
        JSONObject website = new JSONObject();

        try {
            website.put("id", cursor.getString(cursor.getColumnIndex("_id")));
            website.put("pref", false);
            website.put("value", cursor.getString(cursor.getColumnIndex("data1")));
            website.put("type", this.getContactType(cursor.getInt(cursor.getColumnIndex("data2"))));
        } catch (JSONException var4) {
            Log.e("ContactsAccessor", var4.getMessage(), var4);
        }

        return website;
    }

    private JSONObject photoQuery(Cursor cursor, long contactId) {
        JSONObject photo = new JSONObject();

        try {
            photo.put("id", cursor.getString(cursor.getColumnIndex("_id")));
            photo.put("pref", false);
            photo.put("type", "url");
            Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(mContext.getContentResolver(), person);
            String base64Text = "";

            try {
                byte[] buf = new byte[input.available()];
                input.read(buf);
                base64Text = "data:image/png;base64," + Base64.encodeToString(buf, 2);
            } catch (Exception var9) {
                var9.printStackTrace();
            }

            photo.put("value", base64Text);
        } catch (JSONException var10) {
            Log.e("ContactsAccessor", var10.getMessage(), var10);
        }

        return photo;
    }

    private JSONObject populateContact(HashMap<String, Boolean> populate, JSONObject contact, JSONArray organizations, JSONArray addresses, JSONArray phones, JSONArray emails, JSONArray ims, JSONArray websites, JSONArray photos) {
        try {
            if (phones.length() > 0 || this.isRequired("phoneNumbers", populate)) {
                contact.put("phoneNumbers", phones);
            }

            if (emails.length() > 0 || this.isRequired("emails", populate)) {
                contact.put("emails", emails);
            }

            if (addresses.length() > 0 || this.isRequired("addresses", populate)) {
                contact.put("addresses", addresses);
            }

            if (ims.length() > 0 || this.isRequired("ims", populate)) {
                contact.put("ims", ims);
            }

            if (organizations.length() > 0 || this.isRequired("organizations", populate)) {
                contact.put("organizations", organizations);
            }

            if (websites.length() > 0 || this.isRequired("urls", populate)) {
                contact.put("urls", websites);
            }

            if (photos.length() > 0 || this.isRequired("photos", populate)) {
                contact.put("photos", photos);
            }
        } catch (JSONException var11) {
            Log.e("ContactsAccessor", "e.getMessage()==" + var11.getMessage(), var11);
        }

        return contact;
    }


    protected HashMap<String, Boolean> buildPopulationSet(JSONArray fields) {
        HashMap map = new HashMap();

        try {
            if (fields.length() != 0 && (fields.length() != 1 || !fields.getString(0).equals("*"))) {
                for (int i = 0; i < fields.length(); ++i) {
                    String key = fields.getString(i);
                    if (key.startsWith("displayName")) {
                        map.put("displayName", true);
                    } else if (key.startsWith("name")) {
                        map.put("name", true);
                    } else if (key.startsWith("nickname")) {
                        map.put("nickname", true);
                    } else if (key.startsWith("phoneNumbers")) {
                        map.put("phoneNumbers", true);
                    } else if (key.startsWith("emails")) {
                        map.put("emails", true);
                    } else if (key.startsWith("addresses")) {
                        map.put("addresses", true);
                    } else if (key.startsWith("ims")) {
                        map.put("ims", true);
                    } else if (key.startsWith("organizations")) {
                        map.put("organizations", true);
                    } else if (key.startsWith("birthday")) {
                        map.put("birthday", true);
                    } else if (key.startsWith("note")) {
                        map.put("note", true);
                    } else if (key.startsWith("urls")) {
                        map.put("urls", true);
                    } else if (key.startsWith("photos")) {
                        map.put("photos", true);
                    } else if (key.startsWith("categories")) {
                        map.put("categories", true);
                    }
                }
            } else {
                map.put("displayName", true);
                map.put("name", true);
                map.put("nickname", true);
                map.put("phoneNumbers", true);
                map.put("emails", true);
                map.put("addresses", true);
                map.put("ims", true);
                map.put("organizations", true);
                map.put("birthday", true);
                map.put("note", true);
                map.put("urls", true);
                map.put("photos", true);
                map.put("categories", true);
            }
        } catch (JSONException var5) {
            Log.e("ContactsAccessor", var5.getMessage(), var5);
        }

        return map;
    }


    private int getPhoneType(String string) {
        int type = 7;
        if ("home".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 1;
        } else if ("mobile".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 2;
        } else if ("work".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 3;
        } else if ("work fax".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 4;
        } else if ("home fax".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 5;
        } else if ("fax".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 4;
        } else if ("pager".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 6;
        } else if ("other".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 7;
        } else if ("car".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 9;
        } else if ("company main".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 10;
        } else if ("isdn".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 11;
        } else if ("main".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 12;
        } else if ("other fax".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 13;
        } else if ("radio".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 14;
        } else if ("telex".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 15;
        } else if ("work mobile".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 17;
        } else if ("work pager".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 18;
        } else if ("assistant".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 19;
        } else if ("mms".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 20;
        } else if ("callback".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 8;
        } else if ("tty ttd".equals(string.toLowerCase(Locale.ENGLISH))) {
            return 16;
        } else {
            return "custom".equals(string.toLowerCase(Locale.ENGLISH)) ? 0 : type;
        }
    }


    protected boolean isRequired(String key, HashMap<String, Boolean> map) {
        Boolean retVal = (Boolean) map.get(key);
        return retVal == null ? false : retVal;
    }


    private WhereOptions buildIdClause(ArrayList<String> contactIds) {
        WhereOptions options = new WhereOptions();
        Iterator<String> it = contactIds.iterator();
        StringBuffer buffer = new StringBuffer("(");

        while (it.hasNext()) {
            buffer.append("'" + (String) it.next() + "'");
            if (it.hasNext()) {
                buffer.append(",");
            }
        }

        buffer.append(")");
        options.setSelection("raw_contact_id IN " + buffer.toString());
        options.setSelectionArgs((String[]) null);
        return options;
    }

    class WhereOptions {
        private String selection;
        private String[] selectionArgs;

        WhereOptions() {
        }

        public void setSelection(String where) {
            this.selection = where;
        }

        public String getSelection() {
            return this.selection;
        }

        public void setSelectionArgs(String[] whereArgs) {
            this.selectionArgs = whereArgs;
        }

        public String[] getSelectionArgs() {
            return this.selectionArgs;
        }
    }


    private JSONObject organizationQuery(Cursor cursor) {
        JSONObject organization = new JSONObject();

        try {
            organization.put("id", cursor.getString(cursor.getColumnIndex("_id")));
            organization.put("pref", false);
            organization.put("type", this.getOrgType(cursor.getInt(cursor.getColumnIndex("data2"))));
            organization.put("department", cursor.getString(cursor.getColumnIndex("data5")));
            organization.put("name", cursor.getString(cursor.getColumnIndex("data1")));
            organization.put("title", cursor.getString(cursor.getColumnIndex("data4")));
        } catch (JSONException var4) {
            Log.e("ContactsAccessor", var4.getMessage(), var4);
        }

        return organization;
    }


    private String getOrgType(int type) {
        String stringType;
        switch (type) {
            case 0:
                stringType = "custom";
                break;
            case 1:
                stringType = "work";
                break;
            case 2:
            default:
                stringType = "other";
        }

        return stringType;
    }


    private JSONObject addressQuery(Cursor cursor) {
        JSONObject address = new JSONObject();

        try {
            address.put("id", cursor.getString(cursor.getColumnIndex("_id")));
            address.put("pref", false);
            address.put("type", this.getAddressType(cursor.getInt(cursor.getColumnIndex("data2")) + ""));
            address.put("formatted", cursor.getString(cursor.getColumnIndex("data1")));
            address.put("streetAddress", cursor.getString(cursor.getColumnIndex("data4")));
            address.put("locality", cursor.getString(cursor.getColumnIndex("data7")));
            address.put("region", cursor.getString(cursor.getColumnIndex("data8")));
            address.put("postalCode", cursor.getString(cursor.getColumnIndex("data9")));
            address.put("country", cursor.getString(cursor.getColumnIndex("data10")));
        } catch (JSONException var4) {
            Log.e("ContactsAccessor", var4.getMessage(), var4);
        }

        return address;
    }


    private int getAddressType(String string) {
        int type = 3;
        if (string != null) {
            if ("work".equals(string.toLowerCase(Locale.ENGLISH))) {
                return 2;
            }

            if ("other".equals(string.toLowerCase(Locale.ENGLISH))) {
                return 3;
            }

            if ("home".equals(string.toLowerCase(Locale.ENGLISH))) {
                return 1;
            }
        }

        return type;
    }


    private String getContactType(int type) {
        String stringType;
        switch (type) {
            case 0:
                stringType = "custom";
                break;
            case 1:
                stringType = "home";
                break;
            case 2:
                stringType = "work";
                break;
            case 3:
            default:
                stringType = "other";
                break;
            case 4:
                stringType = "mobile";
        }

        return stringType;
    }


}
