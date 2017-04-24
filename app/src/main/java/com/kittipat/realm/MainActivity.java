package com.kittipat.realm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import hugo.weaving.DebugLog;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
public class MainActivity extends AppCompatActivity {

    Realm realm;
    EditText edtFirstName;
    EditText edtLastName;
    EditText edtAge;
    Button btnSave;
    Button btnQuery;
    Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtFirstName = (EditText) findViewById(R.id.edtFirstname);
        edtLastName = (EditText) findViewById(R.id.edtLasttname);
        edtAge = (EditText) findViewById(R.id.edtAge);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnQuery = (Button) findViewById(R.id.btnQuery);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(config);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeRealmTransaction();
            }
        });

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryRealm();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RealmResults<User> results = realm.where(User.class).findAll();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        results.deleteAllFromRealm();
                    }
                });
            }
        });
    }

    @DebugLog
    private String QueryRealm() {
        RealmQuery<User> query = realm.where(User.class);
        String count = String.valueOf(query.count());
        RealmResults<User> name = query.equalTo("firstName","Piyawat").findAll();

        return name.get(0).getFirstName() + " " + name.get(0).getLastName() + " Count " + count;
    }

    private void executeRealmTransaction() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                User user = realm.createObject(User.class);
                user.setFirstName(String.valueOf(edtFirstName.getText()));
                user.setLastName(String.valueOf(edtLastName.getText()));
                user.setAge(Integer.parseInt(String.valueOf(edtAge.getText())));
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this,"Create Complete",Toast.LENGTH_SHORT).show();
                RealmQuery<User> query = realm.where(User.class);
                String firstName = String.valueOf(query.equalTo("firstName",String.valueOf(edtFirstName.getText())));
                String lastName = String.valueOf(query.equalTo("lastName",String.valueOf(edtLastName.getText())));
                executeRealmOnSuccess(firstName,lastName);

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(MainActivity.this,"Create Error",Toast.LENGTH_SHORT).show();

            }
        });
    }

    @DebugLog
    private String executeRealmOnSuccess(String firstName, String lastName) {
        return firstName+" "+lastName;
    }
}
