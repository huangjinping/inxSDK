package cdax.naindex;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cdax.naindex.datalib.ContactsUtil;

public class ContactAccessor {
    private final String TAG = "ContactAccessor";
    private final String GROUP = "group";
    private final String VALUE = "value";
    private final String ID = "id";
    private final String PHONE_NUMBERS = "phoneNumbers";
    private Context mContext;

    public ContactAccessor(Context mContext) {
        this.mContext = mContext;
    }

    public JSONArray getContracts() {


        JSONArray jsonArray = new JSONArray();

        try {
            int limit = 2147483647;
            Cursor idCursor = mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID}, null, null, ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " ASC");
            ArrayList endContactIds = new ArrayList();
            while (idCursor.moveToNext()) {
                String raw_id = idCursor.getString(idCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
                endContactIds.add(raw_id);
            }
            idCursor.close();
            if (endContactIds.size() == 0) {
                return new JSONArray();
            } else {
                WhereOptions idOptions = this.buildIdClause(endContactIds);
                Cursor c = mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI, (String[]) null, idOptions.getSelection(), idOptions.getSelectionArgs(), ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " ASC");
                JSONArray contacts = this.populateContactArray(limit, c);

                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject jsonObject = contacts.getJSONObject(i);
                    JSONArray phoneArray = jsonObject.optJSONArray(PHONE_NUMBERS);
                    if (phoneArray == null) {
                        continue;
                    }
                    for (int j = 0; j < phoneArray.length(); j++) {
                        JSONObject phoneObject = phoneArray.optJSONObject(j);
                        JSONObject item = new JSONObject();
                        item.put("number", phoneObject.optString(VALUE));
                        item.put("contact_display_name", jsonObject.optString(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        item.put("up_time", jsonObject.optLong(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP));
                        item.put("times_contacted", jsonObject.optLong(ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED));
                        item.put("last_time_contacted", jsonObject.optString(ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED));
                        item.put("source", "device");
                        item.put(GROUP, jsonObject.optJSONArray(GROUP));
                        jsonArray.put(item);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private JSONArray populateContactArray(int limit, Cursor c) {
        long rawId = 0L;
        long oldRawId = 0L;
        boolean newContact = true;
        String mimetype = "";
        JSONArray contacts = new JSONArray();
        JSONObject contact = new JSONObject();
        JSONArray phones = new JSONArray();

        if (c.getCount() > 0) {
            while (true) {
                if (!c.moveToNext() || contacts.length() > limit - 1) {
                    if (contacts.length() < limit) {
                        contacts.put(this.populateContact(contact, phones));
                    }
                    break;
                }
                try {
                    rawId = c.getLong(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
                    if (c.getPosition() == 0) {
                        oldRawId = rawId;
                    }
                    if (oldRawId != rawId) {
                        contacts.put(this.populateContact(contact, phones));
                        contact = new JSONObject();
                        phones = new JSONArray();
                        newContact = true;
                    }
                    if (newContact) {
                        newContact = false;
                        contact.put(ID, rawId);
                    }
                    ContactsUtil contactsUtil = new ContactsUtil(mContext);
                    JSONArray jsonArray = contactsUtil.queryGroups(rawId);
                    contact.put(GROUP, jsonArray);
                    contact.put(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP, c.getLong(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP)));
                    contact.put(ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED, c.getLong(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED)));
                    contact.put(ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED, c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED)));
                    mimetype = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.MIMETYPE));
                    if (mimetype.equals(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)) {
                        contact.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME)));
                    }
                    if (mimetype.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                        phones.put(this.phoneQuery(c));
                    }
                } catch (Exception var21) {
                    var21.printStackTrace();
                }
                oldRawId = rawId;
            }
        }

        c.close();
        return contacts;
    }


    private JSONObject phoneQuery(Cursor cursor) {
        JSONObject phoneNumber = new JSONObject();
        try {
            phoneNumber.put(VALUE, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
        } catch (Exception var5) {
            var5.printStackTrace();
        }
        return phoneNumber;
    }

    private JSONObject populateContact(JSONObject contact, JSONArray phones) {
        try {
            if (phones.length() > 0) {
                contact.put(PHONE_NUMBERS, phones);
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }

        return contact;
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
        options.setSelection(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " IN " + buffer.toString());
        options.setSelectionArgs((String[]) null);
        return options;
    }

    class WhereOptions {
        private String selection;
        private String[] selectionArgs;

        WhereOptions() {
        }

        public String getSelection() {
            return this.selection;
        }

        public void setSelection(String where) {
            this.selection = where;
        }

        public String[] getSelectionArgs() {
            return this.selectionArgs;
        }

        public void setSelectionArgs(String[] whereArgs) {
            this.selectionArgs = whereArgs;
        }
    }
}
