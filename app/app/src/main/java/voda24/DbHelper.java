package voda24;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbHelper {

    public static void createDbIfNotExists(Context context) {
        try (SQLiteDatabase db = context.openOrCreateDatabase("app.db", MODE_PRIVATE, null)) {
            db.execSQL("CREATE TABLE IF NOT EXISTS settings (name TEXT, value INTEGER)");
            db.execSQL("INSERT OR IGNORE INTO settings VALUES ('bottle_price', 35)");
        } catch (Exception ignored) {}
    }

    public static void updateBottlePrice(Context context, Integer price) {
        try (SQLiteDatabase db = context.openOrCreateDatabase("app.db", MODE_PRIVATE, null)) {
            db.execSQL("UPDATE settings SET value = " + price);
        } catch (Exception ignored) {}
    }

    public static Integer getActualValue(Context context) {
        try (SQLiteDatabase db = context.openOrCreateDatabase("app.db", MODE_PRIVATE, null)) {
            Cursor query = db.rawQuery("SELECT * FROM settings LIMIT 1;", null);

            while(query.moveToNext()){
                return query.getInt(1);
            }
        } catch (Exception ignored) {}

        return 0;
    }
}
