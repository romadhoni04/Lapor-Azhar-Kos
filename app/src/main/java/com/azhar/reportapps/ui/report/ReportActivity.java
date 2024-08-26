package com.azhar.reportapps.ui.report;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.azhar.reportapps.model.ModelDatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.azhar.reportapps.R;
import com.azhar.reportapps.utils.BitmapManager;
import com.azhar.reportapps.utils.Constant;
import com.azhar.reportapps.viewmodel.InputDataViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    public static final String DATA_TITLE = "TITLE";
    public static final int REQUEST_PICK_PHOTO = 1;
    int REQ_CAMERA = 101;
    File fileDirectoty, imageFilename;
    String strTitle, strTimeStamp, strImageName, strFilePath, strBase64Photo;
    InputDataViewModel inputDataViewModel;
    Toolbar toolbar;
    TextView tvTitle;
    ImageView imageLaporan;
    LinearLayout layoutImage;
    ExtendedFloatingActionButton fabSend;
    EditText inputNama, inputTelepon, inputLokasi, inputTanggal, inputLaporan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        setStatusBar();
        setInitLayout();
        setSendLaporan();
    }

    private void setInitLayout() {
        toolbar = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.tvTitle);
        imageLaporan = findViewById(R.id.imageLaporan);
        layoutImage = findViewById(R.id.layoutImage);
        fabSend = findViewById(R.id.fabSend);
        inputNama = findViewById(R.id.inputNama);
        inputTelepon = findViewById(R.id.inputTelepon);
        inputLokasi = findViewById(R.id.inputLokasi);
        inputTanggal = findViewById(R.id.inputTanggal);
        inputLaporan = findViewById(R.id.inputLaporan);

        // Get data from intent
        strTitle = getIntent().getStringExtra(DATA_TITLE);
        if (strTitle != null) {
            tvTitle.setText(strTitle);
        }

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        inputLokasi.setText(Constant.lokasiPengaduan);

        inputDataViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
                .get(InputDataViewModel.class);

        layoutImage.setOnClickListener(v -> {
            AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
            pictureDialog.setTitle("Upload Foto Laporan");
            String[] pictureDialogItems = {"Pilih foto dari galeri", "Ambil foto lewat kamera"};

            pictureDialog.setItems(pictureDialogItems, (dialog, which) -> {
                switch (which) {
                    case 0:
                        Dexter.withContext(ReportActivity.this)
                                .withPermissions(Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION)
                                .withListener(new MultiplePermissionsListener() {
                                    @Override
                                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                                        if (report.areAllPermissionsGranted()) {
                                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                            startActivityForResult(galleryIntent, REQUEST_PICK_PHOTO);
                                        }
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                        token.continuePermissionRequest();
                                    }
                                }).check();
                        break;
                    case 1:
                        Dexter.withContext(ReportActivity.this)
                                .withPermissions(Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION)
                                .withListener(new MultiplePermissionsListener() {
                                    @Override
                                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                                        if (report.areAllPermissionsGranted()) {
                                            try {
                                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(ReportActivity.this,
                                                        BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
                                                startActivityForResult(intent, REQ_CAMERA);
                                            } catch (IOException ex) {
                                                Toast.makeText(ReportActivity.this, "Gagal membuka kamera!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                        token.continuePermissionRequest();
                                    }
                                }).check();
                        break;
                }
            });
            pictureDialog.show();
        });

        inputTanggal.setOnClickListener(view -> {
            Calendar tanggalJemput = Calendar.getInstance();
            DatePickerDialog.OnDateSetListener date = (view1, year, monthOfYear, dayOfMonth) -> {
                tanggalJemput.set(Calendar.YEAR, year);
                tanggalJemput.set(Calendar.MONTH, monthOfYear);
                tanggalJemput.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String strFormatDefault = "d MMMM yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strFormatDefault, Locale.getDefault());
                inputTanggal.setText(simpleDateFormat.format(tanggalJemput.getTime()));
            };

            new DatePickerDialog(ReportActivity.this, date,
                    tanggalJemput.get(Calendar.YEAR),
                    tanggalJemput.get(Calendar.MONTH),
                    tanggalJemput.get(Calendar.DAY_OF_MONTH)).show();
        });
    }
    private void setSendLaporan() {
        fabSend.setOnClickListener(v -> {
            String strNama = inputNama.getText().toString();
            String strTelepon = inputTelepon.getText().toString();
            String strLokasi = inputLokasi.getText().toString();
            String strTanggal = inputTanggal.getText().toString();
            String strLaporan = inputLaporan.getText().toString();

            if (strFilePath == null || strNama.isEmpty() || strTelepon.isEmpty() || strLokasi.isEmpty() || strTanggal.isEmpty() || strLaporan.isEmpty()) {
                Toast.makeText(ReportActivity.this, "Data tidak boleh ada yang kosong!", Toast.LENGTH_SHORT).show();
            } else {
                // Tentukan nomor WhatsApp sesuai dengan jenis laporan
                String nomorWhatsApp;
                switch (strTitle) {
                    case "Lapor Laundry":
                        nomorWhatsApp = NOMOR_LAPOR_LAUNDRY;
                        break;
                    case "Lapor Apotik":
                        nomorWhatsApp = NOMOR_LAPOR_APOTIK;
                        break;
                    case "Lapor Galon":
                        nomorWhatsApp = NOMOR_LAPOR_GALON;
                        break;
                    default:
                        nomorWhatsApp = NOMOR_LAPOR_LAUNDRY; // Default nomor jika tidak sesuai dengan kategori
                        break;
                }

                // Format pesan
                String pesan = "Nama: " + strNama + "\n" +
                        "Telepon: " + strTelepon + "\n" +
                        "Lokasi: " + strLokasi + "\n" +
                        "Tanggal: " + strTanggal + "\n" +
                        "Laporan: " + strLaporan;

                // Kirim pesan ke WhatsApp
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("https://wa.me/" + nomorWhatsApp + "?text=" + Uri.encode(pesan)));
                startActivity(sendIntent);

                Toast.makeText(ReportActivity.this, "Laporan Anda terkirim, tunggu info selanjutnya ya!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // Tambahkan nomor WhatsApp untuk setiap jenis laporan
    private static final String NOMOR_LAPOR_LAUNDRY = "+6285777441563"; //
    private static final String NOMOR_LAPOR_APOTIK = "+6285747850664"; //
    private static final String NOMOR_LAPOR_GALON = "+6285893171446"; //


    private File createImageFile() throws IOException {
        strTimeStamp = new SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(new Date());
        strImageName = "IMG_" + strTimeStamp + "_";
        fileDirectoty = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "");
        imageFilename = File.createTempFile(strImageName, ".jpg", fileDirectoty);
        strFilePath = imageFilename.getAbsolutePath();
        return imageFilename;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CAMERA && resultCode == RESULT_OK) {
            convertImage(strFilePath);
        } else if (requestCode == REQUEST_PICK_PHOTO && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            if (selectedImage != null) {
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String mediaPath = cursor.getString(columnIndex);
                    cursor.close();
                    strFilePath = mediaPath;
                    convertImage(mediaPath);
                }
            }
        }
    }

    private void convertImage(String imageFilePath) {
        File imageFile = new File(imageFilePath);
        if (imageFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmapImage = BitmapFactory.decodeFile(imageFilePath, options);

            Glide.with(this)
                    .load(bitmapImage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_image_upload)
                    .into(imageLaporan);

            strBase64Photo = BitmapManager.bitmapToBase64(bitmapImage);
        }
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        if (on) {
            layoutParams.flags |= bits;
        } else {
            layoutParams.flags &= ~bits;
        }
        window.setAttributes(layoutParams);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

/* // program yang tersimpan didatabase
private void setSendLaporan() {
        fabSend.setOnClickListener(v -> {
            String strNama = inputNama.getText().toString();
            String strTelepon = inputTelepon.getText().toString();
            String strLokasi = inputLokasi.getText().toString();
            String strTanggal = inputTanggal.getText().toString();
            String strLaporan = inputLaporan.getText().toString();

            if (strFilePath == null || strNama.isEmpty() || strTelepon.isEmpty() || strLokasi.isEmpty() || strTanggal.isEmpty() || strLaporan.isEmpty()) {
                Toast.makeText(ReportActivity.this, "Data tidak boleh ada yang kosong!", Toast.LENGTH_SHORT).show();
            } else {

                ModelDatabase laporan = new ModelDatabase();
                laporan.setNama(strNama);
                laporan.setTelepon(strTelepon);
                laporan.setLokasi(strLokasi);
                laporan.setTanggal(strTanggal);
                laporan.setIsiLaporan(strLaporan);
                laporan.setImage(strBase64Photo);
                laporan.setKategori(strTitle); // Set kategori sesuai dengan title

                inputDataViewModel.insertData(laporan);

                Toast.makeText(ReportActivity.this, "Laporan Anda terkirim dan disimpan!", Toast.LENGTH_SHORT).show();

                // Navigasi ke halaman riwayat
                Intent intent = new Intent(ReportActivity.this, HistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Tambahkan nomor WhatsApp untuk setiap jenis laporan
    private static final String NOMOR_LAPOR_LAUNDRY = "+6285777441563"; //
    private static final String NOMOR_LAPOR_APOTIK = "+6285747850664"; //
    private static final String NOMOR_LAPOR_GALON = "+6285893171446"; //
 */

    /*  //Program untuk mengirim kepada banyak nomer tetapi tidak dapat mengirim gambar//

    private void setSendLaporan() {
        fabSend.setOnClickListener(v -> {
            String strNama = inputNama.getText().toString();
            String strTelepon = inputTelepon.getText().toString();
            String strLokasi = inputLokasi.getText().toString();
            String strTanggal = inputTanggal.getText().toString();
            String strLaporan = inputLaporan.getText().toString();

            if (strFilePath == null || strNama.isEmpty() || strTelepon.isEmpty() || strLokasi.isEmpty() || strTanggal.isEmpty() || strLaporan.isEmpty()) {
                Toast.makeText(ReportActivity.this, "Data tidak boleh ada yang kosong!", Toast.LENGTH_SHORT).show();
            } else {
                // Tentukan nomor WhatsApp sesuai dengan jenis laporan
                String nomorWhatsApp;
                switch (strTitle) {
                    case "Lapor Laundry":
                        nomorWhatsApp = NOMOR_LAPOR_LAUNDRY;
                        break;
                    case "Lapor Apotik":
                        nomorWhatsApp = NOMOR_LAPOR_APOTIK;
                        break;
                    case "Lapor Galon":
                        nomorWhatsApp = NOMOR_LAPOR_GALON;
                        break;
                    default:
                        nomorWhatsApp = NOMOR_LAPOR_LAUNDRY; // Default nomor jika tidak sesuai dengan kategori
                        break;
                }

                // Format pesan
                String pesan = "Nama: " + strNama + "\n" +
                        "Telepon: " + strTelepon + "\n" +
                        "Lokasi: " + strLokasi + "\n" +
                        "Tanggal: " + strTanggal + "\n" +
                        "Laporan: " + strLaporan;

                // Kirim pesan ke WhatsApp
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("https://wa.me/" + nomorWhatsApp + "?text=" + Uri.encode(pesan)));
                startActivity(sendIntent);

                Toast.makeText(ReportActivity.this, "Laporan Anda terkirim, tunggu info selanjutnya ya!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // Tambahkan nomor WhatsApp untuk setiap jenis laporan
    private static final String NOMOR_LAPOR_LAUNDRY = "+6285777441563"; //
    private static final String NOMOR_LAPOR_APOTIK = "+6285747850664"; //
    private static final String NOMOR_LAPOR_GALON = "+6285893171446"; //

     */

    //Bisa Pakai Gambar Tapi NgeBUG
   /* private void setSendLaporan() {
        fabSend.setOnClickListener(v -> {
            String strNama = inputNama.getText().toString();
            String strTelepon = inputTelepon.getText().toString();
            String strLokasi = inputLokasi.getText().toString();
            String strTanggal = inputTanggal.getText().toString();
            String strLaporan = inputLaporan.getText().toString();

            if (strFilePath == null || strNama.isEmpty() || strTelepon.isEmpty() || strLokasi.isEmpty() || strTanggal.isEmpty() || strLaporan.isEmpty()) {
                Toast.makeText(ReportActivity.this, "Data tidak boleh ada yang kosong!", Toast.LENGTH_SHORT).show();
            } else {
                // Tentukan nomor WhatsApp tujuan
                String nomorWhatsApp = "6285777441563"; // Format nomor tanpa tanda +

                // Format pesan untuk WhatsApp
                String pesanWhatsapp = "Nama: " + strNama + "%0A" +
                        "Telepon: " + strTelepon + "%0A" +
                        "Lokasi: " + strLokasi + "%0A" +
                        "Tanggal: " + strTanggal + "%0A" +
                        "Laporan: " + strLaporan;

                // URL untuk mengirim pesan melalui WhatsApp
                String url = "https://wa.me/" + nomorWhatsApp + "?text=" + Uri.encode(pesanWhatsapp);

                // Intent untuk mengirim pesan teks melalui WhatsApp
                Intent sendTextIntent = new Intent(Intent.ACTION_VIEW);
                sendTextIntent.setData(Uri.parse(url));
                sendTextIntent.setPackage("com.whatsapp");

                // Intent untuk mengirim gambar melalui WhatsApp
                Intent sendImageIntent = new Intent(Intent.ACTION_SEND);
                sendImageIntent.setType("image/*");
                File file = new File(strFilePath);
                Uri imageUri = FileProvider.getUriForFile(ReportActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider", file);
                sendImageIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                sendImageIntent.setPackage("com.whatsapp");

                try {
                    // Memulai aktivitas untuk mengirim teks
                    startActivity(sendTextIntent);

                    // Tunggu sebentar untuk memastikan teks terkirim sebelum mengirim gambar
                    new Handler().postDelayed(() -> {
                        try {
                            // Memulai aktivitas untuk mengirim gambar
                            startActivity(sendImageIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(ReportActivity.this, "WhatsApp tidak terinstall!", Toast.LENGTH_SHORT).show();
                        }
                    }, 2000); // Delay 2 detik
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ReportActivity.this, "WhatsApp tidak terinstall!", Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(ReportActivity.this, "Laporan Anda berhasil terkirim!", Toast.LENGTH_SHORT).show();
            }
        });
    } */