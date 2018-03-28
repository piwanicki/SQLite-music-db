package sample.model;


/**
 * Class is a singleton
 */




import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataSource {


    public static final String DB_NAME = "music.db";
    public static final String CONNECTION_STRING = "jdbc:sqlite:C:\\Users\\Jam\\Desktop\\JavaGuit\\MusicDatabaseFX\\" + DB_NAME;

    public static final String TABLE_ALBUMS = "albums";
    public static final String COLUMN_ALBUM_ID = "_id";
    public static final String COLUMN_ALBUM_NAME = "name";
    public static final String COLUMN_ALBUM_ARTIST = "artist";
    public static final int INDEX_ALBUM_ID = 1;
    public static final int INDEX_ALBUM_NAME = 2;
    public static final int INDEX_ALBUM_ARTIST = 3;


    public static final String TABLE_ARTISTS = "artists";
    public static final String COLUMN_ARTIST_ID = "_id";
    public static final String COLUMN_ARTIST_NAME = "name";
    public static final int INDEX_ARTIST_ID = 1;
    public static final int INDEX_ARTIST_NAME = 2;


    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_SONG_TRACK = "track";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_SONG_ALBUM = "album";
    public static final int INDEX_SONG_TRACK = 1;
    public static final int INDEX_SONG_TITLE = 2;
    public static final int INDEX_SONG_ALBUM = 3;

    public static final int ORDER_BY_NONE = 1;
    public static final int ORDER_BY_ASC = 2;
    public static final int ORDER_BY_DESC = 3;

    public static final String ORDER_ALBUMS_BY_ARTIST_AT_START = "SELECT " + TABLE_ALBUMS + '.' + COLUMN_ALBUM_NAME + " FROM " + TABLE_ALBUMS + " INNER JOIN " + TABLE_ARTISTS + " ON "
            + TABLE_ALBUMS + '.' + COLUMN_ALBUM_ARTIST + " = " + TABLE_ARTISTS + '.' + COLUMN_ARTIST_ID + " WHERE " + TABLE_ARTISTS + '.' + COLUMN_ARTIST_NAME + " = \"";

    public static final String ORDER_BY_ALBUM_NAME = "ORDER BY " + TABLE_ALBUMS + '.' + COLUMN_ALBUM_NAME + " COLLATE NOCASE ";

    public static final String ORDER_ARTIST_BY_ALBUMS = "ORDER BY " + TABLE_ARTISTS + '.' + COLUMN_ARTIST_NAME + " COLLATE NOCASE";

    public static final String QUERY_ARTIST_FOR_SONG_START = "SELECT " + TABLE_ARTISTS + '.' + COLUMN_ARTIST_NAME + ", " + TABLE_ALBUMS + '.' + COLUMN_ALBUM_NAME + ", " + TABLE_SONGS + '.' +
            COLUMN_SONG_TRACK + " FROM " + TABLE_SONGS + " INNER JOIN " + TABLE_ALBUMS + " ON " + TABLE_SONGS + '.' + COLUMN_SONG_ALBUM + " = " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ID +
            " INNER JOIN " + TABLE_ARTISTS + " ON " + TABLE_ALBUMS + '.' + COLUMN_ALBUM_ARTIST + " = " + TABLE_ARTISTS + '.' + COLUMN_ARTIST_ID +
            " WHERE " + TABLE_SONGS + '.' + COLUMN_SONG_TITLE + " = \"";

    public static final String TABLE_ARTIST_SONG_VIEW = "artists_list";

    public static final String VIEW_OF_SONGS = "CREATE VIEW IF NOT EXISTS " +
            TABLE_ARTIST_SONG_VIEW + " AS SELECT " + TABLE_ARTISTS + "." + COLUMN_ARTIST_NAME + ", " +
            TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + " AS " + COLUMN_SONG_ALBUM + ", " +
            TABLE_SONGS + "." + COLUMN_SONG_TRACK + ", " + TABLE_SONGS + "." + COLUMN_SONG_TITLE +
            " FROM " + TABLE_SONGS +
            " INNER JOIN " + TABLE_ALBUMS + " ON " + TABLE_SONGS +
            "." + COLUMN_SONG_ALBUM + " = " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ID +
            " INNER JOIN " + TABLE_ARTISTS + " ON " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ARTIST +
            " = " + TABLE_ARTISTS + "." + COLUMN_ARTIST_ID +
            " ORDER BY " +
            TABLE_ARTISTS + "." + COLUMN_ARTIST_NAME + ", " +
            TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + ", " +
            TABLE_SONGS + "." + COLUMN_SONG_TRACK;

    public static final String QUERY_ARTISTS_LIST_OF_VIEW = "SELECT " + COLUMN_ARTIST_NAME + ", " + COLUMN_SONG_ALBUM + ", "
            + COLUMN_SONG_TITLE + ", " + COLUMN_SONG_TRACK + " FROM " + TABLE_ARTIST_SONG_VIEW + " WHERE " + COLUMN_SONG_TITLE + " = \"";


    //We use prepared statement for avoid injection attack ("title" or 1=1 or "")
    // ?  - placeholder, we specify an argument which is used to prepared statement query (we put index in the method)

    public static final String QUERY_VIEW_SONG_INFO_PREPARESTATEMENT = "SELECT " + COLUMN_ARTIST_NAME + ", " + COLUMN_ALBUM_NAME + ", " + COLUMN_SONG_TRACK + ", " +
            COLUMN_SONG_TITLE + " FROM " + TABLE_ARTIST_SONG_VIEW + " WHERE " + COLUMN_SONG_TITLE + " = ?" + " COLLATE NOCASE";


    // PreparedStatement
    //Deklaruje stala dla  zapytania SQL z ?
    //Tworze instancje PreparedStatemnt uzywajac connection.prepareStatement(sqlStatementString)
    //Jesli chcemy uzyc zapytania sql wywowulejmy odpowiednie settery, aby ustawic wartosc dla placeholdera
    //Uruchamiamy zapytanie poprzez PrepareStatement.execute() albo .executeQuery()

    // Transakcje:
    //transakcje pozwalaja stworzyc jedno zapytanie skladajace sie z paru polecen. Jesli jedno z nich zawiedze, transkacja nie wykona sie, nie zapisze i nie wprowadzi zmian w bazie danych,
    // uzywamy ich wtedy kiedy chcemy zmienic dane w bazie danych
    //ACID (Atomicity, Consistiency, Isolation, Durability)
    // Atomicity / niepodzielnosc - ,,wszystko-lub-nic”, transakcja nie może być wykonana częściowo
    // Consistiency / integralnosci - po zatwierdzeniu transakcji muszą być spełnione wszystkie warunki poprawności nałożone na bazę danych
    // Isolation / izolacja - efekt równoległego wykonania dwu lub więcej transakcji musi być szeregowalny
    // Durability / trwalosci - po zakonczeniu operacji jej zmiany zostaja na zawsze

    // BEGIN TRANSACTION - wymuszamy start transakcji
    // END TRANSACTION - zakancza transkacje i commituje zmiany
    // COMMIT - zatwierdzamy zmiany wykonane w transakcji
    // ROLLBACK - anulujemy osttanie zmiany,mozna wykorzystac na ostatnim commicie

    // Wylaczamy autocommit - Connection.setAutoCommit(false)
    // operacje na bd
    // uzywamy Connection.commit()
    // wlaczamy autocommit - Connection.setAutoCommit(true)


    public static final String INSERT_ARTIST = "INSERT INTO " + TABLE_ARTISTS + "(" + COLUMN_ARTIST_NAME + ")" + " VALUES (?)";

    public static final String INSERT_ALBUM = "INSERT INTO " + TABLE_ALBUMS + "(" + COLUMN_ALBUM_NAME + ", " + COLUMN_ALBUM_ARTIST + ")" + " VALUES (?,?)";

    public static final String INSERT_SONG = " INSERT INTO " + TABLE_SONGS + "(" + COLUMN_SONG_TRACK + ", " + COLUMN_SONG_TITLE + ", " + COLUMN_SONG_ALBUM + ")" + " VALUES ( ?,?,?)";


    public static final String QUERY_ARTIST = "SELECT " + COLUMN_ARTIST_ID + " FROM " + TABLE_ARTISTS + " WHERE " + COLUMN_ARTIST_NAME + " =?";
    public static final String QUERY_ALBUM = "SELECT " + COLUMN_ALBUM_ID + " FROM " + TABLE_ALBUMS + " WHERE " + COLUMN_ALBUM_NAME + " =?";
    public static final String QUERY_SONG = "SELECT " + COLUMN_SONG_TITLE + ", " + COLUMN_SONG_ALBUM + " FROM " + TABLE_SONGS + " WHERE " + COLUMN_SONG_TITLE + " =?";

    public static final String QUERY_ALBUMS_BY_ARTIST_ID = "SELECT * FROM " + TABLE_ALBUMS + " WHERE " + COLUMN_ALBUM_ARTIST + " =? " + " ORDER BY " + COLUMN_ALBUM_NAME + " COLLATE NOCASE";
    public static final String QUERY_ALBUM_FOR_ARTIST = " SELECT " + COLUMN_ALBUM_NAME + " FROM " + TABLE_ALBUMS + " WHERE " + COLUMN_ARTIST_NAME + " =?";



    public static List<Artist> artistsList = new ArrayList<>();

    private PreparedStatement queryArtistSongsInfo;
    private PreparedStatement insertIntoArtists;
    private PreparedStatement insertIntoAlbums;
    private PreparedStatement insertIntoSongs;

    private PreparedStatement queryArtist;
    private PreparedStatement queryAlbum;
    private PreparedStatement querySong;

    private PreparedStatement queryAlbumsByArtistID;
    private PreparedStatement queryAlbumForArtist;

    private Connection connection;


    private static DataSource instance = new DataSource();

    private DataSource() {

    }

    public static DataSource getInstance() {

        return instance;
    }

    public boolean open() {

        try {
            connection = DriverManager.getConnection(CONNECTION_STRING);
            queryArtistSongsInfo = connection.prepareStatement(QUERY_VIEW_SONG_INFO_PREPARESTATEMENT);
            insertIntoArtists = connection.prepareStatement(INSERT_ARTIST, Statement.RETURN_GENERATED_KEYS);
            insertIntoAlbums = connection.prepareStatement(INSERT_ALBUM, Statement.RETURN_GENERATED_KEYS);
            insertIntoSongs = connection.prepareStatement(INSERT_SONG);

            queryArtist = connection.prepareStatement(QUERY_ARTIST);
            queryAlbum = connection.prepareStatement(QUERY_ALBUM);
            querySong = connection.prepareStatement(QUERY_SONG);
            queryAlbumsByArtistID = connection.prepareStatement(QUERY_ALBUMS_BY_ARTIST_ID);
            queryAlbumForArtist = connection.prepareStatement(QUERY_ALBUM_FOR_ARTIST);

            return true;
        } catch (SQLException e) {
            System.out.println("Error with SQL loading " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            if (queryArtistSongsInfo != null) {
                queryArtistSongsInfo.close();
            }

            if (insertIntoArtists != null) {
                insertIntoArtists.close();
            }

            if (insertIntoAlbums != null) {
                insertIntoAlbums.close();
            }

            if (insertIntoSongs != null) {
                insertIntoSongs.close();
            }

            if (queryArtist != null) {
                queryArtist.close();
            }

            if (queryAlbum != null) {
                queryAlbum.close();
            }

            if (querySong != null) {
                querySong.close();
            }

            if (queryAlbumsByArtistID != null) {
                queryAlbumsByArtistID.close();
            }

            if(queryAlbumForArtist != null){
                queryAlbumForArtist.close();
            }

            if (connection != null) {
                connection.close();
            }


        } catch (SQLException e) {
            System.out.println("Error with closing SQLConnection " + e.getMessage());
        }
    }

    public List<Artist> queryArtists(int sortOrder) {


        /**
         * First method of declaring statement and results with try/catch block
         */
//        Statement statement = null;
//        ResultSet result = null;
//
//        try {
//            statement = connection.createStatement();
//            result = statement.executeQuery("SELECT * FROM " + TABLE_ARTIST);
//            List<Artist> artists = new ArrayList<>();
//
//            while(result.next()){
//                Artist artist = new Artist();
//                artist.setId(result.getInt(COLUMN_ARTIST_ID));
//                artist.setName(result.getString(COLUMN_ARTIST_NAME));
//                artists.add(artist);
//            }
//
//            return artists;
//
//        } catch(SQLException e ){
//            System.out.println("Problem with query : " + e.getMessage());
//            return null;
//        } finally {
//                try {
//                    if (result != null) {
//                        result.close();
//                    }
//                }catch (SQLException e) {
//                    System.out.println("Problem with closing results " + e.getMessage());
//                }
//                try{
//                    if(statement!=null) {
//                        statement.close();
//                    }
//                } catch (SQLException e){
//                    System.out.println("Problem with closing statement " + e.getMessage());
//                }
//            }
//        }
//    }
        /**
         * Second method of declaring statement and results with try block with resources
         */


        StringBuilder sort = new StringBuilder("SELECT * FROM ");
        sort.append(TABLE_ARTISTS);
        if (sortOrder != ORDER_BY_NONE) {
            sort.append(" ORDER BY ");
            sort.append(COLUMN_ARTIST_NAME);
            sort.append(" COLLATE NOCASE ");
            if (sortOrder == ORDER_BY_ASC) {
                sort.append(" ASC ");
            } else {
                sort.append(" DESC ");
            }
        }

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sort.toString())) {



            while (resultSet.next()) {
                Artist artist = new Artist();
                artist.setId(resultSet.getInt(INDEX_ARTIST_ID));
                artist.setName(resultSet.getString(INDEX_ARTIST_NAME));
                artistsList.add(artist);

                /**
                 *   Thread.sleep is  here only for showing how progBar is working
                 */
                try{
                    Thread.sleep(10);
                } catch (InterruptedException e){
                    System.out.println(e.getMessage());
                }
            }
            return artistsList;

        } catch (SQLException e) {
            System.out.println("Error with statement or resultset " + e.getMessage());
            return null;
        }
    }

    public void printArtist() {
        for (Artist artist : artistsList) {
            System.out.println("ID : " + artist.getId() + ", name : " + artist.getName());
        }
    }

    public List<String> queryAlbumOfArtists(String artistName, int sortOrder) {

        //select albums.name from albums inner join artists on albums.artist = artists._id where artists.name ="Iron Maiden" order by albums.name collate nocase asc

        StringBuilder sb = new StringBuilder(ORDER_ALBUMS_BY_ARTIST_AT_START);
        sb.append(artistName);
        sb.append("\" ");

        if (sortOrder != ORDER_BY_NONE) {
            sb.append(ORDER_BY_ALBUM_NAME);
            if (sortOrder == ORDER_BY_ASC) {
                sb.append(" ASC ");
            } else {
                sb.append(" DESC ");
            }
        } else {
            sb.append(ORDER_ARTIST_BY_ALBUMS);
        }
        System.out.println("SQL statement was : " + sb.toString());

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sb.toString())) {

            List<String> albums = new ArrayList<>();
            while (resultSet.next()) {
                albums.add(resultSet.getString(1));
            }
            return albums;

        } catch (SQLException e) {
            System.out.println("Error with executing query of albums " + e.getMessage());
            return null;
        }
    }

    public List<ArtistsSongs> querySongsOfArtist(String songName, int sortOrder) {

        StringBuilder sb = new StringBuilder(QUERY_ARTIST_FOR_SONG_START);
        sb.append(songName);
        sb.append("\"");

        if (sortOrder != ORDER_BY_NONE) {
            sb.append(ORDER_BY_ALBUM_NAME);
            if (sortOrder == ORDER_BY_ASC) {
                sb.append(" ASC ");
            } else {
                sb.append(" DESC");
            }
        }

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sb.toString())) {

            List<ArtistsSongs> songs = new ArrayList<>();
            while (resultSet.next()) {
                ArtistsSongs artistsSongs = new ArtistsSongs();
                artistsSongs.setAlbumsName(resultSet.getString(2));
                artistsSongs.setArtistName(resultSet.getString(COLUMN_ARTIST_NAME));
                artistsSongs.setSongTrack(resultSet.getString(COLUMN_SONG_TRACK));
                songs.add(artistsSongs);
            }
            return songs;

        } catch (SQLException e) {
            System.out.println("Something went wrong with query songs " + e.getMessage());
            return null;
        }
    }

    public void MetaDataSchema(String table) {

        String sql = "SELECT * FROM " + table;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int numRows = resultSetMetaData.getColumnCount();
            System.out.println("Number of rows in - " + table + " equals : " + numRows);

            for (int i = 1; i <= numRows; i++) {

                System.out.println(i + " column is " + resultSetMetaData.getColumnName(i));
            }

        } catch (SQLException e) {
            System.out.println("Something wrong with counting metadata " + e.getMessage());
        }
    }

    public void countRecords(String table) {

        String sql = "SELECT COUNT(*) FROM " + table;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            int numOfRecords = resultSet.getInt(1);

            System.out.println("Number of records in table : >" + table + "< equals : " + numOfRecords);

        } catch (SQLException e) {
            System.out.println("Something went wrong with counting records " + e.getMessage());
        }
    }


    public boolean createViewOfSongs() {

        try (Statement statement = connection.createStatement()) {
            statement.execute(VIEW_OF_SONGS);
            return true;
        } catch (SQLException e) {
            System.out.println("Something went wrong with creating view of songs " + e.getMessage());
            return false;
        }
    }


    public List<ArtistsSongs> querySongsFromSongsView(String songsTitle) {

        StringBuilder sb = new StringBuilder(QUERY_ARTISTS_LIST_OF_VIEW);
        sb.append(songsTitle);
        sb.append("\"");

        System.out.println(sb.toString());

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sb.toString())) {

            List<ArtistsSongs> querySongs = new ArrayList<>();

            while (resultSet.next()) {
                ArtistsSongs artistsSongs = new ArtistsSongs();

                artistsSongs.setArtistName(resultSet.getString(1));
                artistsSongs.setAlbumsName(resultSet.getString(2));
                artistsSongs.setSongTitle(resultSet.getString(4));
                artistsSongs.setSongTrack(resultSet.getString(3));
                querySongs.add(artistsSongs);
            }
            return querySongs;

        } catch (SQLException e) {
            System.out.println("Something went wrong with query with songs " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<ArtistsSongs> queryArtistSongView(String title) {

        try {
            queryArtistSongsInfo.setString(1, title);
            ResultSet resultSet = queryArtistSongsInfo.executeQuery();

            List<ArtistsSongs> queryArtistList = new ArrayList<>();
            while (resultSet.next()) {
                ArtistsSongs artistsSongs = new ArtistsSongs();
                artistsSongs.setArtistName(resultSet.getString(1));
                artistsSongs.setAlbumsName(resultSet.getString(2));
                artistsSongs.setSongTrack(resultSet.getString(3));
                artistsSongs.setSongTitle(resultSet.getString(4));
                queryArtistList.add(artistsSongs);
            }
            return queryArtistList;

        } catch (SQLException e) {
            System.out.println("something went wrong with queryArtistSongView method " + e.getMessage());
            e.printStackTrace();
            return null;
        }

    }

    public int insertArtist(String name) throws SQLException {

        queryArtist.setString(1, name);
        ResultSet resultSet = queryArtist.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt(1);
        } else {
            insertIntoArtists.setString(1, name);
            int affectedRows = insertIntoArtists.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert artist! " + name);
            }

            ResultSet genKey = insertIntoArtists.getGeneratedKeys();

            if (genKey.next()) {
                return genKey.getInt(1);
            } else {
                throw new SQLException("Couldn't get the _id of artist!");
            }
        }
    }


    public int insertAlbum(String artistName, int artistID) throws SQLException {

        queryAlbum.setString(1, artistName);
        ResultSet resultSet = queryAlbum.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt(1);
        } else {
            insertIntoAlbums.setString(1, artistName);
            insertIntoAlbums.setInt(2, artistID);

            int affectedRows = insertIntoAlbums.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert album with specified id : " + artistName + " , " + artistID);
            }

            ResultSet genKey = insertIntoAlbums.getGeneratedKeys();

            if (genKey.next()) {
                return genKey.getInt(1);
            } else {
                throw new SQLException("Couldn't get album _id");
            }
        }
    }


    public void insertSong(String title, String artist, String albumName, int track) {

        try {

            querySong.setString(1, title);
            ResultSet resultSet = querySong.executeQuery();

            if (resultSet.next()) {
                System.out.println("Song already exist in database");
            } else {

                connection.setAutoCommit(false);

                int artistID = insertArtist(artist);
                int albumID = insertAlbum(albumName, artistID);
                insertIntoSongs.setInt(1, track);
                insertIntoSongs.setString(2, title);
                insertIntoSongs.setInt(3, albumID);

                int affectedRows = insertIntoSongs.executeUpdate();
                if (affectedRows == 1) {
                    connection.commit();
                    System.out.println("Rekord dodany");
                } else {
                    throw new SQLException("Song insert failed!");
                }
            }


        } catch (SQLException e) {
            System.out.println("Something went wrong with inserting song " + e.getMessage());

            try {
                System.out.println("Performing rollback :");
                connection.rollback();
            } catch (SQLException e1) {
                System.out.println("Fatal error!" + e.getMessage());
            }
        } finally {
            try {
                System.out.println("Resetting to default settings");
                connection.setAutoCommit(true);
            } catch (SQLException e2) {
                System.out.println("Problem with seting auto commit to default");
            }
        }
    }


    public List<Album> queryAlbumForArtistID(int id) {

        try {

            queryAlbumsByArtistID.setInt(1, id);
            ResultSet resultSet = queryAlbumsByArtistID.executeQuery();

            List<Album> albums = new ArrayList<>();
            while(resultSet.next()){
                Album album = new Album();
                album.setId(resultSet.getInt(1));
                album.setName(resultSet.getString(2));
                album.setArtistID(id);
                albums.add(album);
            }

            return albums;

        } catch (SQLException e) {
            System.out.println("Error queryAlbumForArtistID method -> " + e.getMessage());
            return null;
        }
    }
}




