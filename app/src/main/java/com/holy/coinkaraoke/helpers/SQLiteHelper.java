package com.holy.coinkaraoke.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.holy.coinkaraoke.models.Post;
import com.holy.coinkaraoke.models.Reservation;
import com.holy.coinkaraoke.models.Song;
import com.holy.coinkaraoke.models.User;

import org.json.JSONArray;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
public class SQLiteHelper extends SQLiteOpenHelper {

    // 데이터베이스 정보
    public static final String DATABASE_NAME = "db";
    public static final int DATABASE_VERSION = 31;

    // 유저 테이블 정보
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_BALANCE = "balance";
    public static final String COLUMN_USER_PHONE = "phone";
    public static final String COLUMN_USER_FAVORITE_SONGS = "favoriteSongs";

    // 예약 테이블 정보
    public static final String TABLE_RESERVATIONS = "reservations";
    public static final String COLUMN_RESERVATION_ID = "id";
    public static final String COLUMN_RESERVATION_ROOM_NUMBER = "roomNumber";
    public static final String COLUMN_RESERVATION_BEGIN_TIME = "beginTime";
    public static final String COLUMN_RESERVATION_END_TIME = "endTime";
    public static final String COLUMN_RESERVATION_USER_ID = "userId";

    // 노래 테이블 정보
    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_SONG_ID = "id";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_SONG_ARTIST = "artist";
    public static final String COLUMN_SONG_ALBUM = "album";
    public static final String COLUMN_SONG_PREFERENCE = "preference";

    // 포스트 테이블 정보
    public static final String TABLE_POSTS = "post";
    public static final String COLUMN_POST_ID = "id";
    public static final String COLUMN_POST_USER_ID = "userId";
    public static final String COLUMN_POST_TITLE = "title";
    public static final String COLUMN_POST_MESSAGE = "message";
    public static final String COLUMN_POST_YOUTUBE_URL = "youtubeUrl";
    public static final String COLUMN_POST_UPLOAD_TIME = "uploadTime";
    public static final String COLUMN_POST_LIKES = "likes";
    public static final String COLUMN_POST_LIKED_USERS = "likedUsers";


    // 데이터베이스 헬퍼 객체
    private static SQLiteHelper instance;

    // 데이터베이스 헬퍼 객체 구하기.
    public static synchronized SQLiteHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SQLiteHelper(context.getApplicationContext());
        }
        return instance;
    }

    // 생성자
    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // 유저 테이블 생성
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS
                + "("
                + COLUMN_USER_ID + " TEXT PRIMARY KEY, "
                + COLUMN_USER_PASSWORD + " TEXT NOT NULL, "
                + COLUMN_USER_NAME + " TEXT NOT NULL, "
                + COLUMN_USER_BALANCE + " INTEGER NOT NULL, "
                + COLUMN_USER_PHONE + " TEXT NOT NULL, "
                + COLUMN_USER_FAVORITE_SONGS + " TEXT"
                + ")";

        db.execSQL(CREATE_USERS_TABLE);

        // 예약 테이블 생성
        String CREATE_RESERVATIONS_TABLE = "CREATE TABLE " + TABLE_RESERVATIONS
                + "("
                + COLUMN_RESERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_RESERVATION_ROOM_NUMBER + " INTEGER NOT NULL, "
                + COLUMN_RESERVATION_BEGIN_TIME + " TEXT NOT NULL, "
                + COLUMN_RESERVATION_END_TIME + " TEXT NOT NULL, "
                + COLUMN_RESERVATION_USER_ID + " TEXT NOT NULL"
                + ")";

        db.execSQL(CREATE_RESERVATIONS_TABLE);

        // 노래 테이블 생성
        String CREATE_SONGS_TABLE = "CREATE TABLE " + TABLE_SONGS
                + "("
                + COLUMN_SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SONG_TITLE + " TEXT NOT NULL, "
                + COLUMN_SONG_ARTIST + " TEXT NOT NULL, "
                + COLUMN_SONG_ALBUM + " TEXT NOT NULL, "
                + COLUMN_SONG_PREFERENCE + " INTEGER NOT NULL"
                + ")";

        db.execSQL(CREATE_SONGS_TABLE);

        // 포스트 테이블 생성
        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_POSTS
                + "("
                + COLUMN_POST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_POST_USER_ID + " TEXT NOT NULL, "
                + COLUMN_POST_TITLE + " TEXT NOT NULL, "
                + COLUMN_POST_MESSAGE + " TEXT NOT NULL, "
                + COLUMN_POST_YOUTUBE_URL + " TEXT NOT NULL, "
                + COLUMN_POST_UPLOAD_TIME + " TEXT NOT NULL, "
                + COLUMN_POST_LIKES + " INTEGER NOT NULL, "
                + COLUMN_POST_LIKED_USERS + " TEXT NOT NULL"
                + ")";

        db.execSQL(CREATE_POSTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // 버전 교체
        if (newVersion != oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
            onCreate(db);
        }
    }


    // 유저 등록
    public void addUser(User user) {

        // 쓰기용 DB 를 연다.
        SQLiteDatabase db = getWritableDatabase();

        // DB 입력 시작
        db.beginTransaction();
        try {
            // 유저 정보를 모두 values 객체에 입력한다
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID, user.getId());
            values.put(COLUMN_USER_PASSWORD, user.getPassword());
            values.put(COLUMN_USER_NAME, user.getName());
            values.put(COLUMN_USER_BALANCE, user.getBalance());
            values.put(COLUMN_USER_PHONE, user.getPhone());
            values.put(COLUMN_USER_FAVORITE_SONGS, user.getFavoriteSongs().toString());

            // 데이터베이스에 values 를 입력한다.
            db.insertOrThrow(TABLE_USERS, null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    // 유저 계좌 업데이트
    public void updateUserBalance(String id, int balance) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_BALANCE, balance);

        db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?",
                new String[]{id});
    }

    // 유저 계좌 송금
    public void transferUserBalance(String fromId, String toId, int amount) {

        User from = getUser(fromId);
        User to = getUser(toId);

        if (from == null || to == null) {
            return;
        }

        if (from.getBalance() < amount) {
            return;
        }

        updateUserBalance(fromId, from.getBalance() - amount);
        updateUserBalance(toId, to.getBalance() + amount);
    }

    // 유저 Favorite Song 업데이트
    public void updateUserFavoriteSongs(String userId, List<Integer> favoriteSongs) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_FAVORITE_SONGS, favoriteSongs.toString());

        db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?",
                new String[]{userId});
    }

    public boolean insertUserFavoriteSong(String userId, int songId) {

        User user = getUser(userId);

        // Failure: User not exists
        if (user == null) {
            return false;
        }

        // Get favorite song list of the user
        List<Integer> favoriteSongs = user.getFavoriteSongs();

        // Failure: The song already in the list
        if (favoriteSongs.contains(songId)) {
            return false;
        }

        // Insert the song in the list
        favoriteSongs.add(songId);
        updateUserFavoriteSongs(userId, favoriteSongs);

        return true;
    }

    public boolean removeUserFavoriteSong(String userId, int songId) {

        User user = getUser(userId);

        // Failure: User not exists
        if (user == null) {
            return false;
        }

        // Get favorite song list of the user
        List<Integer> favoriteSongs = user.getFavoriteSongs();

        // Failure: The song already in the list
        if (!favoriteSongs.contains(songId)) {
            return false;
        }

        // Remove the song from the list
        favoriteSongs.remove((Integer)songId);
        updateUserFavoriteSongs(userId, favoriteSongs);

        return true;
    }

    // 아이디로 유저 검색
    public User getUser(String id) {

        User user = null;

        // 읽기용 DB 를 연다.
        SQLiteDatabase db = getReadableDatabase();

        // 지정된 id 를 갖는 유저 데이터를 가리키는 커서를 검색한다.
        String SELECT_USER =
                "SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_USER_ID + " = '" + id + "'";

        Cursor cursor = db.rawQuery(SELECT_USER, null);

        try {
            if (cursor.moveToFirst()) {
                // 커서로부터 유저 데이터를 가져온다.
                String password = cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME));
                int balance = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_BALANCE));
                String phone = cursor.getString(cursor.getColumnIndex(COLUMN_USER_PHONE));

                String strFavoriteSongs = cursor.getString(cursor.getColumnIndex(COLUMN_USER_FAVORITE_SONGS));
                JSONArray jsonArray = new JSONArray(strFavoriteSongs);
                List<Integer> favoriteSongs = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    int songId = jsonArray.getInt(i);
                    favoriteSongs.add(songId);
                }

                // 유저 데이터로 유저 객체를 만들어 리턴한다.
                user = new User(id, password, name, balance, phone, favoriteSongs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return user;
    }

    public User getUserBy(String phone) {

        User user = null;

        // 읽기용 DB 를 연다.
        SQLiteDatabase db = getReadableDatabase();

        // 지정된 id 를 갖는 유저 데이터를 가리키는 커서를 검색한다.
        String SELECT_USER =
                "SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_USER_PHONE + " = '" + phone + "'";

        Cursor cursor = db.rawQuery(SELECT_USER, null);

        try {
            if (cursor.moveToFirst()) {
                // 커서로부터 유저 데이터를 가져온다.
                String id = cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID));
                String password = cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME));
                int balance = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_BALANCE));

                String strFavoriteSongs = cursor.getString(cursor.getColumnIndex(COLUMN_USER_FAVORITE_SONGS));
                JSONArray jsonArray = new JSONArray(strFavoriteSongs);
                List<Integer> favoriteSongs = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    int songId = jsonArray.getInt(i);
                    favoriteSongs.add(songId);
                }

                // 유저 데이터로 유저 객체를 만들어 리턴한다.
                user = new User(id, password, name, balance, phone, favoriteSongs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return user;
    }

    // 모든 아이디 가져오기
    public List<User> getAllUsers() {

        List<User> userList = new ArrayList<>();

        // 읽기용 DB 를 연다.
        SQLiteDatabase db = getReadableDatabase();

        // 모든 데이터를 가리키는 커서를 검색한다.
        String SELECT_ALL_USERS = "SELECT * FROM " + TABLE_USERS;

        Cursor cursor = db.rawQuery(SELECT_ALL_USERS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID));
                    String password = cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD));
                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME));
                    int balance = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_BALANCE));
                    String phone = cursor.getString(cursor.getColumnIndex(COLUMN_USER_PHONE));

                    String strFavoriteSongs = cursor.getString(cursor.getColumnIndex(COLUMN_USER_FAVORITE_SONGS));
                    JSONArray jsonArray = new JSONArray(strFavoriteSongs);
                    List<Integer> favoriteSongs = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        int songId = jsonArray.getInt(i);
                        favoriteSongs.add(songId);
                    }

                    // 유저 데이터로 유저 객체를 만들어 리스트에 삽입한다.
                    User user = new User(id, password, name, balance, phone, favoriteSongs);
                    userList.add(user);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return userList;
    }


    // 예약 생성
    public void addReservation(Reservation reservation) {

        // 쓰기용 DB 를 연다.
        SQLiteDatabase db = getWritableDatabase();

        // DB 입력 시작
        db.beginTransaction();
        try {
            // 예약 정보를 모두 values 객체에 입력한다
            ContentValues values = new ContentValues();
            values.put(COLUMN_RESERVATION_ROOM_NUMBER, reservation.getRoomNumber());
            values.put(COLUMN_RESERVATION_BEGIN_TIME, reservation.getBeginTime().toString());
            values.put(COLUMN_RESERVATION_END_TIME, reservation.getEndTime().toString());
            values.put(COLUMN_RESERVATION_USER_ID, reservation.getUserId());

            // 데이터베이스에 values 를 입력한다.
            db.insertOrThrow(TABLE_RESERVATIONS, null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    // 예약 검색 (아이디로)
    public Reservation getReservation(int id) {

        Reservation reservation = null;

        // 읽기용 DB 를 연다.
        SQLiteDatabase db = getReadableDatabase();

        // 지정된 id 를 갖는 예약 데이터를 가리키는 커서를 검색한다.
        String SELECT_ROOM =
                "SELECT * FROM " + TABLE_RESERVATIONS +
                        " WHERE " + COLUMN_RESERVATION_ID + " = " + id;

        Cursor cursor = db.rawQuery(SELECT_ROOM, null);

        try {
            if (cursor.moveToFirst()) {
                // 커서로부터 예약 데이터를 가져온다.
                int roomNumber = cursor.getInt(cursor.getColumnIndex(COLUMN_RESERVATION_ROOM_NUMBER));
                String strBeginTime = cursor.getString(cursor.getColumnIndex(COLUMN_RESERVATION_BEGIN_TIME));
                String strEndTime = cursor.getString(cursor.getColumnIndex(COLUMN_RESERVATION_END_TIME));
                String userId = cursor.getString(cursor.getColumnIndex(COLUMN_RESERVATION_USER_ID));

                LocalDateTime beginTime = LocalDateTime.parse(strBeginTime);
                LocalDateTime endTime = LocalDateTime.parse(strEndTime);

                // 룸 데이터로 룸 객체를 만들어 리턴한다
                reservation = new Reservation(id, roomNumber, beginTime, endTime, userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return reservation;
    }

    // 예약 검색 (방 번호로)
    public List<Reservation> getReservationsBy(int roomNumber) {

        List<Reservation> reservationList = new ArrayList<>();

        // 읽기용 DB 를 연다.
        SQLiteDatabase db = getReadableDatabase();

        // 지정된 방 번호를 갖는 예약 데이터를 가리키는 커서를 검색한다.
        String SELECT_RESERVATIONS =
                "SELECT * FROM " + TABLE_RESERVATIONS +
                        " WHERE " + COLUMN_RESERVATION_ROOM_NUMBER + " = " + roomNumber +
                        " ORDER BY " + COLUMN_RESERVATION_BEGIN_TIME;

        Cursor cursor = db.rawQuery(SELECT_RESERVATIONS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서로부터 예약 데이터를 가져온다.
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_RESERVATION_ID));
                    String strBeginTime = cursor.getString(cursor.getColumnIndex(COLUMN_RESERVATION_BEGIN_TIME));
                    String strEndTime = cursor.getString(cursor.getColumnIndex(COLUMN_RESERVATION_END_TIME));
                    String userId = cursor.getString(cursor.getColumnIndex(COLUMN_RESERVATION_USER_ID));

                    LocalDateTime beginTime = LocalDateTime.parse(strBeginTime);
                    LocalDateTime endTime = LocalDateTime.parse(strEndTime);

                    // 이미 지난 예약은 조회하지 않음
                    if (endTime.isBefore(LocalDateTime.now())) {
                        continue;
                    }

                    // 예약 데이터로 예약 객체를 만들어 리스트에 추가한다
                    Reservation reservation = new Reservation(id, roomNumber, beginTime, endTime, userId);
                    reservationList.add(reservation);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return reservationList;
    }

    // 예약 검색 (유저 아이디로)
    public List<Reservation> getReservationsBy(String userId) {

        List<Reservation> reservationList = new ArrayList<>();

        // 읽기용 DB 를 연다.
        SQLiteDatabase db = getReadableDatabase();

        // 지정된 방 번호를 갖는 예약 데이터를 가리키는 커서를 검색한다.
        String SELECT_RESERVATIONS =
                "SELECT * FROM " + TABLE_RESERVATIONS +
                        " WHERE " + COLUMN_RESERVATION_USER_ID + " = '" + userId + "'";

        Cursor cursor = db.rawQuery(SELECT_RESERVATIONS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서로부터 예약 데이터를 가져온다.
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_RESERVATION_ID));
                    int roomNumber = cursor.getInt(cursor.getColumnIndex(COLUMN_RESERVATION_ROOM_NUMBER));
                    String strBeginTime = cursor.getString(cursor.getColumnIndex(COLUMN_RESERVATION_BEGIN_TIME));
                    String strEndTime = cursor.getString(cursor.getColumnIndex(COLUMN_RESERVATION_END_TIME));

                    LocalDateTime beginTime = LocalDateTime.parse(strBeginTime);
                    LocalDateTime endTime = LocalDateTime.parse(strEndTime);

                    // 이미 지난 예약은 조회하지 않음
                    if (endTime.isBefore(LocalDateTime.now())) {
                        continue;
                    }

                    // 예약 데이터로 예약 객체를 만들어 리스트에 추가한다
                    Reservation reservation = new Reservation(id, roomNumber, beginTime, endTime, userId);
                    reservationList.add(reservation);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return reservationList;
    }


    // 노래 추가
    public void addSong(Song song) {

        // 쓰기용 DB 를 연다.
        SQLiteDatabase db = getWritableDatabase();

        // DB 입력 시작
        db.beginTransaction();
        try {
            // 노래 정보를 모두 values 객체에 입력한다
            ContentValues values = new ContentValues();
            values.put(COLUMN_SONG_TITLE, song.getTitle());
            values.put(COLUMN_SONG_ARTIST, song.getArtist());
            values.put(COLUMN_SONG_ALBUM, song.getAlbum());
            values.put(COLUMN_SONG_PREFERENCE, song.getPreference());

            // 데이터베이스에 values 를 입력한다.
            db.insertOrThrow(TABLE_SONGS, null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    // 노래 검색 (아이디로)
    public Song getSong(int id) {

        Song song = null;

        // 읽기용 DB 를 연다.
        SQLiteDatabase db = getReadableDatabase();

        // 지정된 id 를 갖는 노래 데이터를 가리키는 커서를 검색한다.
        String SELECT_SONG =
                "SELECT * FROM " + TABLE_SONGS +
                        " WHERE " + COLUMN_SONG_ID + " = " + id;

        Cursor cursor = db.rawQuery(SELECT_SONG, null);

        try {
            if (cursor.moveToFirst()) {
                // 커서로부터 노래 데이터를 가져온다.
                String strTitle = cursor.getString(cursor.getColumnIndex(COLUMN_SONG_TITLE));
                String strArtist = cursor.getString(cursor.getColumnIndex(COLUMN_SONG_ARTIST));
                String strAlbum = cursor.getString(cursor.getColumnIndex(COLUMN_SONG_ALBUM));
                int preference = cursor.getInt(cursor.getColumnIndex(COLUMN_SONG_PREFERENCE));

                // 노래 데이터로 노래 객체를 만들어 리턴한다
                song = new Song(id, strTitle, strArtist, strAlbum, preference);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return song;
    }

    // 모든 노래 검색
    public List<Song> getAllSongs() {

        List<Song> songList = new ArrayList<>();

        // 읽기용 DB 를 연다.
        SQLiteDatabase db = getReadableDatabase();

        // 모든 노래 데이터를 가리키는 커서를 검색한다.
        String SELECT_ALL_SONGS = "SELECT * FROM " + TABLE_SONGS;

        Cursor cursor = db.rawQuery(SELECT_ALL_SONGS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서로부터 노래 데이터를 가져온다.
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_SONG_ID));
                    String strTitle = cursor.getString(cursor.getColumnIndex(COLUMN_SONG_TITLE));
                    String strArtist = cursor.getString(cursor.getColumnIndex(COLUMN_SONG_ARTIST));
                    String strAlbum = cursor.getString(cursor.getColumnIndex(COLUMN_SONG_ALBUM));
                    int preference = cursor.getInt(cursor.getColumnIndex(COLUMN_SONG_PREFERENCE));

                    // 노래 데이터로 노래 객체를 만들어 리스트에 삽입한다
                    Song song = new Song(id, strTitle, strArtist, strAlbum, preference);
                    songList.add(song);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return songList;
    }

    // 모든 노래 preference 순 검색
    public List<Song> getRankedSongs() {

        List<Song> songList = new ArrayList<>();

        // 읽기용 DB 를 연다.
        SQLiteDatabase db = getReadableDatabase();

        // 모든 노래 데이터를 가리키는 커서를 검색한다.
        String SELECT_ALL_SONGS = "SELECT * FROM " + TABLE_SONGS
                + " ORDER BY " + COLUMN_SONG_PREFERENCE + " DESC";

        Cursor cursor = db.rawQuery(SELECT_ALL_SONGS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서로부터 노래 데이터를 가져온다.
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_SONG_ID));
                    String strTitle = cursor.getString(cursor.getColumnIndex(COLUMN_SONG_TITLE));
                    String strArtist = cursor.getString(cursor.getColumnIndex(COLUMN_SONG_ARTIST));
                    String strAlbum = cursor.getString(cursor.getColumnIndex(COLUMN_SONG_ALBUM));
                    int preference = cursor.getInt(cursor.getColumnIndex(COLUMN_SONG_PREFERENCE));

                    // 노래 데이터로 노래 객체를 만들어 리스트에 삽입한다
                    Song song = new Song(id, strTitle, strArtist, strAlbum, preference);
                    songList.add(song);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return songList;
    }

    // Favorite 노래 검색
    public List<Song> getFavoriteSongs(String userId) {

        List<Song> songList = new ArrayList<>();

        User user = getUser(userId);
        if (user == null) {
            return null;
        }

        List<Integer> favoriteSongs = user.getFavoriteSongs();
        for (int songId : favoriteSongs) {
            Song song = getSong(songId);
            if (song != null) {
                songList.add(song);
            }
        }

        return songList;
    }


    // 포스트 추가
    public void addPost(Post post) {

        // 쓰기용 DB 를 연다.
        SQLiteDatabase db = getWritableDatabase();

        // DB 입력 시작
        db.beginTransaction();
        try {
            // 포스트 정보를 모두 values 객체에 입력한다
            ContentValues values = new ContentValues();
            values.put(COLUMN_POST_USER_ID, post.getUserId());
            values.put(COLUMN_POST_TITLE, post.getTitle());
            values.put(COLUMN_POST_MESSAGE, post.getMessage());
            values.put(COLUMN_POST_YOUTUBE_URL, post.getYoutubeUrl());
            values.put(COLUMN_POST_UPLOAD_TIME, post.getUploadTime().toString());
            values.put(COLUMN_POST_LIKES, post.getLikes());
            values.put(COLUMN_POST_LIKED_USERS, post.getLikedUsers().toString());

            // 데이터베이스에 values 를 입력한다.
            db.insertOrThrow(TABLE_POSTS, null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    // 포스트 검색 (아이디로)
    public Post getPost(int id) {

        Post post = null;

        // 읽기용 DB 를 연다.
        SQLiteDatabase db = getReadableDatabase();

        // 지정된 id 를 갖는 포스트 데이터를 가리키는 커서를 검색한다.
        String SELECT_POST =
                "SELECT * FROM " + TABLE_POSTS +
                        " WHERE " + COLUMN_POST_ID + " = " + id;

        Cursor cursor = db.rawQuery(SELECT_POST, null);

        try {
            if (cursor.moveToFirst()) {
                // 커서로부터 포스트 데이터를 가져온다.
                String userId = cursor.getString(cursor.getColumnIndex(COLUMN_POST_USER_ID));
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_POST_TITLE));
                String message = cursor.getString(cursor.getColumnIndex(COLUMN_POST_MESSAGE));
                String youtubeUrl = cursor.getString(cursor.getColumnIndex(COLUMN_POST_YOUTUBE_URL));
                String strUploadTime = cursor.getString(cursor.getColumnIndex(COLUMN_POST_UPLOAD_TIME));
                int likes = cursor.getInt(cursor.getColumnIndex(COLUMN_POST_LIKES));

                String strLikedUsers = cursor.getString(cursor.getColumnIndex(COLUMN_POST_LIKED_USERS));
                JSONArray jsonArray = new JSONArray(strLikedUsers);
                List<String> likedUsers = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    likedUsers.add(jsonArray.getString(i));
                }

                LocalDateTime uploadTime = LocalDateTime.parse(strUploadTime);

                // 노래 데이터로 노래 객체를 만들어 리턴한다
                post = new Post(id, userId, title, message, youtubeUrl, uploadTime, likes, likedUsers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return post;
    }

    // 포스트 검색 (최신순)
    public List<Post> getNewPosts() {

        List<Post> postList = new ArrayList<>();

        // 읽기용 DB 를 연다.
        SQLiteDatabase db = getReadableDatabase();

        // 모든 포스트 데이터를 가리키는 커서를 검색한다.
        String SELECT_ALL_POSTS = "SELECT * FROM " + TABLE_POSTS
                + " ORDER BY " + COLUMN_POST_UPLOAD_TIME + " DESC";


        Cursor cursor = db.rawQuery(SELECT_ALL_POSTS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서로부터 포스트 데이터를 가져온다.
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_POST_ID));
                    String userId = cursor.getString(cursor.getColumnIndex(COLUMN_POST_USER_ID));
                    String title = cursor.getString(cursor.getColumnIndex(COLUMN_POST_TITLE));
                    String message = cursor.getString(cursor.getColumnIndex(COLUMN_POST_MESSAGE));
                    String youtubeUrl = cursor.getString(cursor.getColumnIndex(COLUMN_POST_YOUTUBE_URL));
                    String strUploadTime = cursor.getString(cursor.getColumnIndex(COLUMN_POST_UPLOAD_TIME));
                    int likes = cursor.getInt(cursor.getColumnIndex(COLUMN_POST_LIKES));

                    // 업로드 시간이 이번 주인지 조사한다
                    LocalDateTime uploadTime = LocalDateTime.parse(strUploadTime);
                    LocalDateTime now = LocalDateTime.now();
                    int dayOfWeek = now.getDayOfWeek().getValue();
                    LocalDateTime thisMonday = now.minusDays(dayOfWeek - 1).withHour(0).withMinute(0);
                    if (uploadTime.isBefore(thisMonday)) {
                        continue;
                    }

                    // likedUsers 를 파싱한다
                    String strLikedUsers = cursor.getString(cursor.getColumnIndex(COLUMN_POST_LIKED_USERS));
                    JSONArray jsonArray = new JSONArray(strLikedUsers);
                    List<String> likedUsers = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        likedUsers.add(jsonArray.getString(i));
                    }

                    // 포스트 데이터로 포스트 객체를 만들어 리스트에 삽입한다
                    Post post = new Post(id, userId, title, message, youtubeUrl, uploadTime, likes, likedUsers);
                    postList.add(post);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return postList;
    }

    // 포스트 검색 (Likes 순)
    public List<Post> getHotPosts() {

        List<Post> postList = new ArrayList<>();

        // 읽기용 DB 를 연다.
        SQLiteDatabase db = getReadableDatabase();

        // 모든 포스트 데이터를 가리키는 커서를 검색한다.
        String SELECT_ALL_POSTS = "SELECT * FROM " + TABLE_POSTS
                + " ORDER BY " + COLUMN_POST_LIKES + " DESC";

        Cursor cursor = db.rawQuery(SELECT_ALL_POSTS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서로부터 포스트 데이터를 가져온다.
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_POST_ID));
                    String userId = cursor.getString(cursor.getColumnIndex(COLUMN_POST_USER_ID));
                    String title = cursor.getString(cursor.getColumnIndex(COLUMN_POST_TITLE));
                    String message = cursor.getString(cursor.getColumnIndex(COLUMN_POST_MESSAGE));
                    String youtubeUrl = cursor.getString(cursor.getColumnIndex(COLUMN_POST_YOUTUBE_URL));
                    String strUploadTime = cursor.getString(cursor.getColumnIndex(COLUMN_POST_UPLOAD_TIME));
                    int likes = cursor.getInt(cursor.getColumnIndex(COLUMN_POST_LIKES));

                    // 업로드 시간이 이번 주인지 조사한다
                    LocalDateTime uploadTime = LocalDateTime.parse(strUploadTime);
                    LocalDateTime now = LocalDateTime.now();
                    int dayOfWeek = now.getDayOfWeek().getValue();
                    LocalDateTime thisMonday = now.minusDays(dayOfWeek - 1).withHour(0).withMinute(0);
                    if (uploadTime.isBefore(thisMonday)) {
                        continue;
                    }

                    // likedUsers 를 파싱한다
                    String strLikedUsers = cursor.getString(cursor.getColumnIndex(COLUMN_POST_LIKED_USERS));
                    JSONArray jsonArray = new JSONArray(strLikedUsers);
                    List<String> likedUsers = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        likedUsers.add(jsonArray.getString(i));
                    }

                    // 포스트 데이터로 포스트 객체를 만들어 리스트에 삽입한다
                    Post post = new Post(id, userId, title, message, youtubeUrl, uploadTime, likes, likedUsers);
                    postList.add(post);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return postList;
    }

    public void likePost(int postId, String userId) {

        Post post = getPost(postId);
        if (post == null) {
            return;
        }

        List<String> likedUsers = post.getLikedUsers();
        likedUsers.add(userId);

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_POST_LIKES, post.getLikes() + 1);
        values.put(COLUMN_POST_LIKED_USERS, likedUsers.toString());

        db.update(TABLE_POSTS, values, COLUMN_POST_ID + " = ?",
                new String[]{ String.valueOf(postId) });
    }

}



