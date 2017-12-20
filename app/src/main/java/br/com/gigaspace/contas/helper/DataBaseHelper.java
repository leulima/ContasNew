package br.com.gigaspace.contas.helper;

import java.io.*;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.util.Log;
import br.com.gigaspace.contas.contract.CategoryContract;
import br.com.gigaspace.contas.contract.AccountContract;
import br.com.gigaspace.contas.contract.SubcategoryContract;
import br.com.gigaspace.contas.model.Constants;
import br.com.gigaspace.contas.model.TransactionTypeEnum;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static String TAG = DataBaseHelper.class.getSimpleName(); // Tag just for the LogCat window

    //destination path (location) of our database on device
    private static String DB_PATH = "";

    private SQLiteDatabase mDataBase;
    private final Context mContext;

    public DataBaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);

        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        Log.i(TAG, "Database Path: " + DB_PATH);

        this.mContext = context;

        try {
            openDataBase();
        }
        catch (SQLException e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * By calling this method and empty database will be created into the default system path
     * of your application so we are gonna be able to overwrite that database with our database.
     * */
    public void createDataBase() throws IOException {
        Log.i(TAG, "Creating database...");

        //If database not exists copy it from the assets
        boolean mDataBaseExist = checkDataBase();

        if (!mDataBaseExist) {
            // By calling this method and empty database will be created into the default system path
            // of your application so we are gonna be able to overwrite that database with our database.
            //this.getReadableDatabase();
            this.getWritableDatabase();
            this.close();

            try {
                // Copy the database from assests
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            }
            catch (IOException e) {
                Log.e(TAG, "Error: " + e.getMessage());
                //throw new RuntimeException("Error creating source database", e);
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        //Check that the database exists here: /data/data/your package/databases/Da Name
        File dbFile = new File(DB_PATH + Constants.DATABASE_NAME);
        Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    /**
     * This method opens the data base connection.
     * First it create the path up till data base of the device.
     * Then create connection with data base.
     */
    public boolean openDataBase() throws SQLException {
        Log.i(TAG, "Opening database...");
        String mPath = DB_PATH + Constants.DATABASE_NAME;

        Log.i(TAG, mPath);

        File dbFile = mContext.getDatabasePath(Constants.DATABASE_NAME);

        if (!dbFile.exists()) {
            try {
                createDataBase();
            } catch (IOException e) {
                throw new RuntimeException("Error creating source database", e);
            }
        }

        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READWRITE);

        return mDataBase != null;
    }

    /**
     * Open the database
     * @throws SQLException
     */
    protected void open() throws SQLException {
        // Opens the database connection to provide the access
        try {
            openDataBase();
            close();

            //mDb = mDbHelper.getReadableDatabase();
            mDataBase = this.getWritableDatabase();
        }
        catch (SQLException mSQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    /**
     * Return o SQLite Database
     * @return
     */
    public SQLiteDatabase getDatabase() {
        if (mDataBase == null) {
            openDataBase();
        }
        return mDataBase;
    }

    /**
     * Checks if the database asset needs to be copied and if so copies it to the
     * default location.
     *
     * @throws IOException
     */
    /*
    private void checkExists() throws IOException {
        Log.i(TAG, "checkExists()");

        File dbFile = new File(DB_PATH);

        if (!dbFile.exists()) {

            Log.i(TAG, "creating database..");

            dbFile.getParentFile().mkdirs();
            copyStream(mContext.getAssets().open(assetPath), new FileOutputStream(
                    dbFile));

            Log.i(TAG, assetPath + " has been copied to " + dbFile.getAbsolutePath());
        }

    }
    */

    private void copyStream(InputStream is, OutputStream os) throws IOException {
        byte buf[] = new byte[1024];
        int c = 0;
        while (true) {
            c = is.read(buf);
            if (c == -1)
                break;
            os.write(buf, 0, c);
        }
        is.close();
        os.close();
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transferring byte stream.
     * */
    private void copyDataBase() throws IOException {
        //Open your local db as the input stream
        InputStream myInput = mContext.getAssets().open(Constants.DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + Constants.DATABASE_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    // This method will execute once in the application entire life cycle
    // All table creation code should put here
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Criando tabela " + AccountContract.TABLE_NAME);

        String sql = "CREATE TABLE " + AccountContract.TABLE_NAME + " (" +
                AccountContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AccountContract.COLUMN_NAME_NOME + " TEXT, " +
                AccountContract.COLUMN_NAME_TIPO + " INTEGER, " +
                AccountContract.COLUMN_NAME_NUMERO + " TEXT, " +
                AccountContract.COLUMN_NAME_VALOR + " REAL)";
        db.execSQL(sql);

        Log.i(TAG, "Criando tabela " + CategoryContract.TABLE_NAME);

        sql = "CREATE TABLE " + CategoryContract.TABLE_NAME + " (" +
                CategoryContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CategoryContract.COLUMN_NAME_TIPO + " TEXT, " +
                CategoryContract.COLUMN_NAME_DESCRICAO + " TEXT)";
        db.execSQL(sql);

        Log.i(TAG, "Criando tabela " + SubcategoryContract.TABLE_NAME);

        sql = "CREATE TABLE " + SubcategoryContract.TABLE_NAME + " (" +
                SubcategoryContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SubcategoryContract.COLUMN_NAME_CATEG_ID + " INTEGER, " +
                SubcategoryContract.COLUMN_NAME_DESCRICAO + " TEXT)";
        db.execSQL(sql);

        createDefaultCategories(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Apagando tabela " + AccountContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AccountContract.TABLE_NAME);

        Log.i(TAG, "Apagando tabela " + CategoryContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CategoryContract.TABLE_NAME);

        Log.i(TAG, "Apagando tabela " + SubcategoryContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SubcategoryContract.TABLE_NAME);

        onCreate(db);
    }

    /**
     * This Method is used to close the data base connection.
     */
    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();

        exportDB();
    }

    /**
     * Create initial categories
     */
    private void createDefaultCategories(SQLiteDatabase database) {
        Log.i(TAG, "Populando tabela de categoria/subcategorias");

        // Insere categorias padrao
        String sqlInsertCategory = "INSERT INTO "+ CategoryContract.TABLE_NAME +" VALUES (?,?,?);";
        String sqlInsertSubcategory = "INSERT INTO "+ SubcategoryContract.TABLE_NAME +" VALUES (?,?,?);";

        SQLiteStatement statementCategory = database.compileStatement(sqlInsertCategory);
        SQLiteStatement statementSubcategory = database.compileStatement(sqlInsertSubcategory);

        database.beginTransaction();
        long idCateg = 1;

        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.CREDITO.getId(), "Bancos", new String[] { "Aplicações", "Empréstimos", "Rendimentos"});
        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.CREDITO.getId(), "Salários", new String[] { "FGTS", "Remuneração", "Horas Extras", "Seguro Desemprego", "Outros" });
        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.CREDITO.getId(), "Horas Extras");
        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.CREDITO.getId(), "Imposto de Renda", new String[] { "Restituição"});

        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.DEBITO.getId(), "Alimentação", new String[] { "Lanchonetes", "Supermercados", "Restaurantes" });
        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.DEBITO.getId(), "Automóvel", new String[] { "Combustível", "Acessórios", "Manutenção", "IPVA", "Seguros", "Estacionamento", "Multas", "Outros"});
        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.DEBITO.getId(), "Casa", new String[] { "Móveis", "Reforma", "Eletrônicos", "Eletrodomésticos", "Outros" });
        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.DEBITO.getId(), "Computação", new String[] { "Hardware", "Software", "Hospedagem", "Livros"});
        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.DEBITO.getId(), "Contas", new String[] { "Água", "Energia", "Aluguel", "Celular", "Internet", "Telefone", "TV a cabo", "Pensão Alimentícia" });
        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.DEBITO.getId(), "Educação", new String[] { "Escola", "Livros", "Revistras", "Concursos", "Cursos", "Papelaria"});
        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.DEBITO.getId(), "Diversão", new String[] { "Bares", "Cinemas", "Moteis", "Locadoras", "Academia", "Clubes", "Outros"});
        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.DEBITO.getId(), "Outros");
        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.DEBITO.getId(), "Pessoal", new String[] { "Beleza", "Cabelo", "Calçados", "Presentes", "Roupas", "Acessórios"});
        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.DEBITO.getId(), "Saúde", new String[] { "Farmácias", "Dentistas", "Médicos", "Remédios", "Plano de saúde", "Hospitais", "Exames"});
        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.DEBITO.getId(), "Bancos", new String[] { "Juros", "Serviços", "Anuidades"});
        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.DEBITO.getId(), "Imóvel", new String[] { "Condomínio", "Impostos", "Financiamento", "Mantençãoo", "Outros"});
        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.DEBITO.getId(), "Impostos", new String[] { "Fazenda", "CPMF", "IPTU", "ISS"});
        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.DEBITO.getId(), "Viagens", new String[] { "Passagens", "Serviços", "Passaportes", "Hotéis", "Táxis"});
        insertCategory(statementCategory, statementSubcategory, idCateg++, TransactionTypeEnum.DEBITO.getId(), "Empresa", new String[] { "Honorários", "Tributos"});

        database.setTransactionSuccessful();
        database.endTransaction();
    }

    private void insertCategory(SQLiteStatement statementCategory, SQLiteStatement statementSubcategory, Long id, String type, String description) {
        insertCategory(statementCategory, statementSubcategory, id, type, description, null);
    }

    private void insertCategory(SQLiteStatement statementCategory, SQLiteStatement statementSubcategory, Long id, String type, String description, String[] subcategories) {
        Log.i(TAG, "Inserindo categoria " + description);
        statementCategory.clearBindings();
        statementCategory.bindLong(1, id);
        statementCategory.bindString(2, type);
        statementCategory.bindString(3, description);
        statementCategory.execute();

        if (subcategories != null) {
            for (String descricao : subcategories) {
                Log.i(TAG, "-> Inserindo subcategoria " + descricao);
                statementSubcategory.clearBindings();
                statementSubcategory.bindLong(2, id);
                statementSubcategory.bindString(3, descricao);
                statementSubcategory.execute();
            }
        }
    }

    private void exportDB() {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = DB_PATH + Constants.DATABASE_NAME;
        String backupDBPath = Constants.DATABASE_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            //Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}