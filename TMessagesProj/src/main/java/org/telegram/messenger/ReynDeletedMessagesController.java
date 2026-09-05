package org.telegram.messenger;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

public class ReynDeletedMessagesController {

    private static final String PREFS = "reyn_deleted_messages";
    private static final String KEY_MESSAGES = "messages";

    public static void save(int account, long dialogId, MessageObject message) {
        if (message == null || message.messageOwner == null) {
            return;
        }

        String text = message.messageOwner.message;

        if (text == null || text.isEmpty()) {
            return;
        }

        SharedPreferences prefs =
                MessagesController.getMainSettings(account);

        String oldData = prefs.getString(KEY_MESSAGES, "[]");

        try {
            JSONArray array = new JSONArray(oldData);

            JSONObject item = new JSONObject();
            item.put("dialog_id", dialogId);
            item.put("message_id", message.getId());
            item.put("text", text);
            item.put("date", message.messageOwner.date);
            item.put("deleted_at", System.currentTimeMillis());

            array.put(item);

            // Не даём журналу бесконечно расти.
            while (array.length() > 1000) {
                array.remove(0);
            }

            prefs.edit()
                    .putString(KEY_MESSAGES, array.toString())
                    .apply();

        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static JSONArray getAll(int account) {
        SharedPreferences prefs =
                MessagesController.getMainSettings(account);

        try {
            return new JSONArray(
                    prefs.getString(KEY_MESSAGES, "[]")
            );
        } catch (Exception e) {
            FileLog.e(e);
            return new JSONArray();
        }
    }

    public static void clear(int account) {
        MessagesController.getMainSettings(account)
                .edit()
                .remove(KEY_MESSAGES)
                .apply();
    }
}
