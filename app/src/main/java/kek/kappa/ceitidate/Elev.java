package kek.kappa.ceitidate;

import android.view.View;
import android.widget.Toast;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Elev extends MainActivity {
    String idnp;
    String date_html;
    DateElev date;

    public Elev(String idnp) {
        this.idnp = idnp;
    }

    public JSONObject getJSON() {
        return date.getJSON();
    }

    public void processIDNP() {
        CookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(MainActivity.context));
        OkHttpClient client = new OkHttpClient().newBuilder().followRedirects(true)
                .cookieJar(cookieJar)
                .build();
        MediaType html = MediaType.parse("application/x-www-form-urlencoded");

        StringBuilder data = new StringBuilder();
        data.append("idnp=");
        data.append(idnp);

        RequestBody body = RequestBody.create(data.toString(), html);

        Request req = new Request.Builder()
                .url("http://api.ceiti.md/date/login")
                .post(body)
                .build();

            client.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    new Thread() {
                        @Override
                        public void run() {
                            changeViewVisibility(false);
                            showToast("Nu am putut contacta host-ul!\nVerifica accesul la internet!");
                        }
                    }.start();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()){

                        date_html = response.body().string();

                        if(date_html.contains("IDNP format din 13 cifre")) {
                            new Thread() {
                                @Override
                                public void run() {
                                    changeViewVisibility(false);
                                    showToast("IDNP Gresit :^(");
                                }
                            }.start();
                            return;
                        }
                        date = new DateElev(date_html);

                        MainActivity.ProgressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.ProgressBar.setVisibility(View.GONE);
                            }

                        });
                        finish();
                        MainActivity.initContent();
                    }
            }});
    }


    private static class DateElev {
        Document soup;
        JSONObject DateJSON;

        public DateElev(String html) {
            this.soup = Jsoup.parse(html);
            try {
                JSONify();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public JSONObject getJSON() {
            return this.DateJSON;
        }

        private void JSONify() throws IllegalAccessException, NoSuchFieldException, JSONException {
            DateJSON = new JSONObject();
            String[] atribute = getAttributes();
            for (String atribut : atribute) {
                String nume_titlu = soup.select("a[aria-controls=" + atribut + "]").text();
                DateJSON.put(nume_titlu, "");
                if (!soup.select("#" + atribut + " .panel-title").isEmpty()) { // Daca sunt submeniuri
                    Elements nume_subcategorii = soup.select("#" + atribut + " .panel-title");
                    Elements tabele = soup.select("#" + atribut + " div[role=tabpanel] table");
                    ArrayList<String> HeaderNames = new ArrayList<>();
                    JSONObject subcategorii = new JSONObject();
                    for (int i = 0; i < nume_subcategorii.toArray().length; i++) {
                        JSONArray JSONArrayRow = new JSONArray();
                        Elements rows = tabele.get(i).select("tr");
                        for (Element r : rows) {
                            // We get the <th> header names
                            if (!r.select("th").isEmpty()) {
                                HeaderNames.clear();
                                Elements headers = r.select("th");
                                for (Element header : headers) {
                                    HeaderNames.add(header.text());
                                }
                            }
                            // Wrapper pentru absente :)
                            if(HeaderNames.get(0).equals("Absen??e totale"))
                            {
                                JSONObject AbsenteOBJ = new JSONObject();
                                JSONArray AbsenteArr = new JSONArray();
                                JSONObject AbsenteOBJrow = new JSONObject();
                                AbsenteOBJrow.put(HeaderNames.get(0),HeaderNames.get(1));
                                r = r.nextElementSibling();
                                AbsenteArr.put(AbsenteOBJrow);
                                for (int j=0;j<3;j++){
                                    AbsenteOBJrow = new JSONObject();
                                    Elements TableData = r.select("td");
                                    AbsenteOBJrow.put(TableData.get(0).text(),TableData.get(1).text());
                                    AbsenteArr.put(AbsenteOBJrow);
                                    r = r.nextElementSibling();
                                }
                                AbsenteOBJ.put("Absente",AbsenteArr);
                                JSONArrayRow.put(AbsenteOBJ);
                                break;
                            }

                            Elements TableData = r.select("td");
                            JSONObject DataObject = new JSONObject();
                            for (int j = 0; j < TableData.toArray().length; j++) {
                                if (HeaderNames.get(j).equals("Semestrul II"))
                                    HeaderNames.set(j,"Denumire");
                                if (HeaderNames.get(j).equals("Denumirea Obiectelor"))
                                    HeaderNames.set(j,"Denumire Obiect");

                                // Handle pentru note, le facem JSONArray :^)
                                if(HeaderNames.get(j).equals("Note"))
                                {
                                    JSONArray ArrayNote = new JSONArray();
                                    String[] Note = TableData.get(j).text().split("\\s*,\\s*");
                                    for(String nota : Note)
                                        ArrayNote.put(nota);
                                    DataObject.put(HeaderNames.get(j), ArrayNote);
                                    continue;
                                }

                                DataObject.put(HeaderNames.get(j), TableData.get(j).text());
                            }

                            if (!(DataObject.length() == 0))
                                JSONArrayRow.put(DataObject);
                        }
                        subcategorii.put(nume_subcategorii.get(i).text(), JSONArrayRow);
                    }
                    DateJSON.put(nume_titlu, subcategorii);
                    continue;
                }
                Element tabel = soup.select("div[id=" + atribut + "] table").first();
                if(atribut.equals("date-personale"))
                {
                    Elements rows = tabel.select("tr");
                    JSONObject DatePersonale = new JSONObject();
                    for (Element r : rows)
                        DatePersonale.put(r.child(0).text(),r.child(1).text());
                    DateJSON.put(nume_titlu,DatePersonale);
                    continue;
                }
                Elements rows = tabel.select("tr");
                ArrayList<String> HeaderNames = new ArrayList<>();
                JSONArray Arr = new JSONArray();
                for (Element r : rows) {
                    JSONObject Obj = new JSONObject();
                    // We get the <th> header names
                    if (!r.select("th").isEmpty()) {
                        HeaderNames.clear();
                        Elements headers = r.select("th");
                        for (Element header : headers) {
                            HeaderNames.add(header.text());
                        }
                    }
                    Elements TableData = r.select("td");

                    for (int j = 0; j < TableData.toArray().length; j++) {
                        if (HeaderNames.get(j).equals("Denumirea Obiectelor"))
                            HeaderNames.set(j,"Denumire Obiect");
                        Obj.put(HeaderNames.get(j), TableData.get(j).text());
                    }
                    if (!(Obj.length() == 0))
                        Arr.put(Obj);
                }
                DateJSON.put(nume_titlu,Arr);
            }
        }

        private String[] getAttributes() {
            Elements ul = soup.select("ul[role=tablist] li");
            String[] attributes = new String[ul.toArray().length];
            for (int i = 0; i < ul.toArray().length; i++)
                attributes[i] = ul.get(i).select("> a").attr("aria-controls");

            return attributes;
        }

    }
}