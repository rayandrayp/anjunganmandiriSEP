/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * DlgAdmin.java
 *
 * Created on 04 Des 13, 12:59:34
 */
package khanzahmsanjungan;

import bridging.ApiBPJS;
import bridging.BPJSCekHistoriPelayanan;
import bridging.BPJSCekNoKartu;
import bridging.BPJSCekReferensiDokterDPJP;
import bridging.BPJSCekReferensiDokterDPJP1;
import bridging.BPJSCekReferensiPenyakit;
import bridging.BPJSCekReferensiPoli;
import bridging.BPJSCekRiwayatRujukanTerakhir;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JOptionPane;
import org.bouncycastle.crypto.engines.TnepresEngine;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 *
 * @author Kode
 */
public class DlgRegistrasiSEPPertama extends javax.swing.JDialog {

    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps, ps3;
    private ResultSet rs, rs3;
    private ApiBPJS api = new ApiBPJS();
    private BPJSCekReferensiDokterDPJP1 dokter = new BPJSCekReferensiDokterDPJP1(null, true);
    private BPJSCekReferensiPenyakit penyakit = new BPJSCekReferensiPenyakit(null, true);
    private DlgCariPoliBPJS poli = new DlgCariPoliBPJS(null, true);
    private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");
    private BPJSCekRiwayatRujukanTerakhir rujukanterakhir = new BPJSCekRiwayatRujukanTerakhir(null, true);
    private BPJSCekHistoriPelayanan historiPelayanan = new BPJSCekHistoriPelayanan(null, true);
    private String umur = "0", sttsumur = "Th", hari = "", kode_dokter = "", kode_poli = "", nama_instansi, alamat_instansi, kabupaten, propinsi, kontak, email;
    private String kdkel = "", kdkec = "", kdkab = "", kdprop = "", nosisrute = "", BASENOREG = "", URUTNOREG = "", link = "", klg = "SAUDARA", statuspasien = "", pengurutan = "", tahun = "", bulan = "", posisitahun = "", awalantahun = "", awalanbulan = "",
            no_ktp = "", tmp_lahir = "", nm_ibu = "", alamat = "", pekerjaan = "", no_tlp = "", tglkkl = "0000-00-00",
            umurdaftar = "0", namakeluarga = "", no_peserta = "", kelurahan = "", kecamatan = "", datajam = "", jamselesai = "", jammulai = "",
            kabupatenpj = "", hariawal = "", requestJson,requestJson2, URL = "", nosep = "", user = "", prb = "", peserta = "", kodedokterreg = "", kodepolireg = "",
            status = "Baru", utc = "", jeniskunjungan = "", nomorreg = "", urlaplikasi = "", urlfinger = "", userfinger = "", passfinger = "",
            tampilkantni = Sequel.cariIsi("select tampilkan_tni_polri from set_tni_polri"),jenisKunjungan="",nomorReferensi="", tgl_rujukan = "", kd_poli_rujukan = "", inisial_petugas = "ASM";
    private int kuota = 0;
    private Properties prop = new Properties();
    private File file;
    private DlgCariPoli poli2 = new DlgCariPoli(null, true);

    private FileWriter fileWriter;
    private String iyem;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root,root2;
    private JsonNode response,response2;
    private FileReader myObj;
    private Calendar cal = Calendar.getInstance();
    private boolean statusfinger = false,checkinMJKN = false;
    private HttpHeaders headers,headers2;
    private HttpEntity requestEntity,requestEntity2;
    private JsonNode nameNode,nameNode2;
    private int day = cal.get(Calendar.DAY_OF_WEEK);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date parsedDate;

    /**
     * Creates new form DlgAdmin
     *
     * @param parent
     * @param id
     */
    public DlgRegistrasiSEPPertama(java.awt.Frame parent, boolean id) {
        super(parent, id);
        initComponents();

        try {
            ps = koneksi.prepareStatement(
                    "select nm_pasien,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) asal,"
                    + "namakeluarga,keluarga,pasien.kd_pj,penjab.png_jawab,if(tgl_daftar=?,'Baru','Lama') as daftar, "
                    + "TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) as tahun, "
                    + "(TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12)) as bulan, "
                    + "TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()) as hari from pasien "
                    + "inner join kelurahan inner join kecamatan inner join kabupaten inner join penjab "
                    + "on pasien.kd_kel=kelurahan.kd_kel and pasien.kd_pj=penjab.kd_pj "
                    + "and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab "
                    + "where pasien.no_rkm_medis=?");
        } catch (Exception ex) {
            System.out.println(ex);
        }
        
        TNoRM.setDocument(new batasInput((byte)6).getKata(TNoRM));

        try {
            ps = koneksi.prepareStatement("select nama_instansi, alamat_instansi, kabupaten, propinsi, aktifkan, wallpaper,kontak,email,logo from setting");
            try {            
                rs = ps.executeQuery();
                while (rs.next()) {
                    nama_instansi = rs.getString("nama_instansi");
                    alamat_instansi = rs.getString("alamat_instansi");
                    kabupaten = rs.getString("kabupaten");
                    propinsi = rs.getString("propinsi");
                    kontak = rs.getString("kontak");
                    email = rs.getString("email");
                }
            } catch (Exception ex) {
                System.out.println(ex);
            } finally {
                if (rs != null) {
                    rs.close();
                }

                if (ps != null) {
                    ps.close();
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        } 

        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (dokter.getTable().getSelectedRow() != -1) {
                    KdDPJP.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                    NmDPJP.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 2).toString());
                    if (JenisPelayanan.getSelectedIndex() == 1) {
                        KdDPJPLayanan.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                        NmDPJPLayanan.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 2).toString());
                    }
                    KdDPJP.requestFocus();

                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        poli.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (poli.getTable().getSelectedRow() != -1) {
                    KdPoli.setText(poli.getTable().getValueAt(poli.getTable().getSelectedRow(), 0).toString());
                    NmPoli.setText(poli.getTable().getValueAt(poli.getTable().getSelectedRow(), 1).toString());
                    KdDPJP.requestFocus();
                    CekProsedur();
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        penyakit.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (penyakit.getTable().getSelectedRow() != -1) {

                    KdPenyakit.setText(penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(), 1).toString());
                    NmPenyakit.setText(penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(), 2).toString());
                    KdPenyakit.requestFocus();

                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        rujukanterakhir.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (rujukanterakhir.getTable().getSelectedRow() != -1) {
                    KdPenyakit.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 0).toString());
                    NmPenyakit.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 1).toString());
                    NoRujukan.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 2).toString());
                    KdPoli.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 3).toString());
                    NmPoli.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 4).toString());
                    KdPpkRujukan.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 6).toString());
                    NmPpkRujukan.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 7).toString());
                    Valid.SetTgl(TanggalRujuk, rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(), 5).toString());
                    if(KdPoli.getText().equals("HDL")){
//                        System.out.println("Pasien HD");
                        getRujukanKhusus(NoKartu.getText());
                        CekProsedur();
                    }
                    Catatan.requestFocus();
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        historiPelayanan.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (historiPelayanan.getTable().getSelectedRow() != -1) {
                    if ((historiPelayanan.getTable().getSelectedColumn() == 6) || (historiPelayanan.getTable().getSelectedColumn() == 7)) {
                        NoRujukan.setText(historiPelayanan.getTable().getValueAt(historiPelayanan.getTable().getSelectedRow(), historiPelayanan.getTable().getSelectedColumn()).toString());
                    }
                }
                NoRujukan.requestFocus();
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            link = prop.getProperty("URLAPIBPJS");
            URUTNOREG = prop.getProperty("URUTNOREG");
            BASENOREG = prop.getProperty("BASENOREG");
        } catch (Exception ex) {

            URUTNOREG = "";
            BASENOREG = "";
        }

        try {
            KdPPK.setText(Sequel.cariIsi("select setting.kode_ppk from setting"));
            NmPPK.setText(Sequel.cariIsi("select setting.nama_instansi from setting"));
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            link = koneksiDB.URLAPIBPJS();
            urlfinger = koneksiDB.URLFINGERPRINTBPJS();
            userfinger = koneksiDB.USERFINGERPRINTBPJS();
            passfinger = koneksiDB.PASSWORDFINGERPRINTBPJS();
            urlaplikasi = koneksiDB.URLAPLIKASIFINGERPRINTBPJS();
            inisial_petugas = koneksiDB.INISIALPETUGAS();
        } catch (Exception e) {
            System.out.println("E : " + e);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LblKdPoli = new component.Label();
        LblKdDokter = new component.Label();
        NoReg = new component.TextBox();
        NoRawat = new component.TextBox();
        Biaya = new component.TextBox();
        TAlmt = new component.Label();
        TPngJwb = new component.Label();
        THbngn = new component.Label();
        NoTelpPasien = new component.Label();
        kdpoli = new widget.TextBox();
        TBiaya = new widget.TextBox();
        Kdpnj = new widget.TextBox();
        nmpnj = new widget.TextBox();
        TNoRw = new widget.TextBox();
        NoRujukMasuk = new widget.TextBox();
        Tanggal = new widget.Tanggal();
        jPanel1 = new component.Panel();
        jPanel3 = new javax.swing.JPanel();
        btnSimpan = new component.Button();
        btnFingerPrint = new component.Button();
        btnKeluar = new component.Button();
        internalFrame2 = new widget.InternalFrame();
        scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        NoKartu = new widget.TextBox();
        jLabel20 = new widget.Label();
        TanggalSEP = new widget.Tanggal();
        jLabel22 = new widget.Label();
        TanggalRujuk = new widget.Tanggal();
        jLabel23 = new widget.Label();
        NoRujukan = new widget.TextBox();
        jLabel9 = new widget.Label();
        KdPPK = new widget.TextBox();
        NmPPK = new widget.TextBox();
        jLabel10 = new widget.Label();
        KdPpkRujukan = new widget.TextBox();
        NmPpkRujukan = new widget.TextBox();
        jLabel11 = new widget.Label();
        KdPenyakit = new widget.TextBox();
        NmPenyakit = new widget.TextBox();
        NmPoli = new widget.TextBox();
        KdPoli = new widget.TextBox();
        LabelPoli = new widget.Label();
        jLabel13 = new widget.Label();
        jLabel14 = new widget.Label();
        Catatan = new widget.TextBox();
        JenisPelayanan = new widget.ComboBox();
        LabelKelas = new widget.Label();
        Kelas = new widget.ComboBox();
        LakaLantas = new widget.ComboBox();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        jLabel18 = new widget.Label();
        JK = new widget.TextBox();
        jLabel24 = new widget.Label();
        JenisPeserta = new widget.TextBox();
        jLabel25 = new widget.Label();
        Status = new widget.TextBox();
        jLabel27 = new widget.Label();
        AsalRujukan = new widget.ComboBox();
        NoTelp = new widget.TextBox();
        Katarak = new widget.ComboBox();
        jLabel37 = new widget.Label();
        jLabel38 = new widget.Label();
        TanggalKKL = new widget.Tanggal();
        LabelPoli2 = new widget.Label();
        KdDPJP = new widget.TextBox();
        NmDPJP = new widget.TextBox();
        jLabel36 = new widget.Label();
        Keterangan = new widget.TextBox();
        jLabel40 = new widget.Label();
        Suplesi = new widget.ComboBox();
        NoSEPSuplesi = new widget.TextBox();
        jLabel41 = new widget.Label();
        LabelPoli3 = new widget.Label();
        KdPropinsi = new widget.TextBox();
        NmPropinsi = new widget.TextBox();
        LabelPoli4 = new widget.Label();
        KdKabupaten = new widget.TextBox();
        NmKabupaten = new widget.TextBox();
        LabelPoli5 = new widget.Label();
        KdKecamatan = new widget.TextBox();
        NmKecamatan = new widget.TextBox();
        jLabel42 = new widget.Label();
        TujuanKunjungan = new widget.ComboBox();
        FlagProsedur = new widget.ComboBox();
        jLabel43 = new widget.Label();
        jLabel44 = new widget.Label();
        Penunjang = new widget.ComboBox();
        jLabel45 = new widget.Label();
        AsesmenPoli = new widget.ComboBox();
        LabelPoli6 = new widget.Label();
        KdDPJPLayanan = new widget.TextBox();
        NmDPJPLayanan = new widget.TextBox();
        btnDPJPLayanan = new widget.Button();
        jLabel55 = new widget.Label();
        jLabel56 = new widget.Label();
        jLabel12 = new widget.Label();
        jLabel6 = new widget.Label();
        NoSKDP = new widget.TextBox();
        jLabel26 = new widget.Label();
        NIK = new widget.TextBox();
        jLabel7 = new widget.Label();
        btnDPJPLayanan1 = new widget.Button();
        btnDiagnosaAwal = new widget.Button();
        btnDiagnosaAwal1 = new widget.Button();
        btnDiagnosaAwal2 = new widget.Button();

        LblKdPoli.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LblKdPoli.setText("Norm");
        LblKdPoli.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        LblKdPoli.setPreferredSize(new java.awt.Dimension(20, 14));

        LblKdDokter.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LblKdDokter.setText("Norm");
        LblKdDokter.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        LblKdDokter.setPreferredSize(new java.awt.Dimension(20, 14));

        NoReg.setPreferredSize(new java.awt.Dimension(320, 30));
        NoReg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NoRegActionPerformed(evt);
            }
        });
        NoReg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoRegKeyPressed(evt);
            }
        });

        NoRawat.setPreferredSize(new java.awt.Dimension(320, 30));
        NoRawat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NoRawatActionPerformed(evt);
            }
        });
        NoRawat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoRawatKeyPressed(evt);
            }
        });

        Biaya.setPreferredSize(new java.awt.Dimension(320, 30));
        Biaya.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BiayaActionPerformed(evt);
            }
        });
        Biaya.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BiayaKeyPressed(evt);
            }
        });

        TAlmt.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        TAlmt.setText("Norm");
        TAlmt.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        TAlmt.setPreferredSize(new java.awt.Dimension(20, 14));

        TPngJwb.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        TPngJwb.setText("Norm");
        TPngJwb.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        TPngJwb.setPreferredSize(new java.awt.Dimension(20, 14));

        THbngn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        THbngn.setText("Norm");
        THbngn.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        THbngn.setPreferredSize(new java.awt.Dimension(20, 14));

        NoTelpPasien.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        NoTelpPasien.setText("Norm");
        NoTelpPasien.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        NoTelpPasien.setPreferredSize(new java.awt.Dimension(20, 14));

        kdpoli.setHighlighter(null);
        kdpoli.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdpoliKeyPressed(evt);
            }
        });

        TBiaya.setText("0");
        TBiaya.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TBiayaKeyPressed(evt);
            }
        });

        Kdpnj.setHighlighter(null);
        Kdpnj.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdpnjKeyPressed(evt);
            }
        });

        nmpnj.setHighlighter(null);
        nmpnj.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nmpnjKeyPressed(evt);
            }
        });

        TNoRw.setText("0");
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });

        NoRujukMasuk.setText("0");
        NoRujukMasuk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoRujukMasukKeyPressed(evt);
            }
        });

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "31-07-2023" }));
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        Tanggal.setOpaque(false);
        Tanggal.setPreferredSize(new java.awt.Dimension(95, 23));
        Tanggal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKeyPressed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new java.awt.BorderLayout(1, 1));

        jPanel1.setBackground(new java.awt.Color(238, 238, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(238, 238, 255), 1, true), "DATA ELIGIBILITAS PESERTA JKN", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Poppins", 0, 24), new java.awt.Color(0, 131, 62))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(400, 70));
        jPanel1.setLayout(new java.awt.BorderLayout(0, 1));

        jPanel3.setBackground(new java.awt.Color(238, 238, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(615, 180));

        btnSimpan.setForeground(new java.awt.Color(0, 131, 62));
        btnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/konfirmasi.png"))); // NOI18N
        btnSimpan.setMnemonic('S');
        btnSimpan.setText("Konfirmasi");
        btnSimpan.setToolTipText("Alt+S");
        btnSimpan.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        btnSimpan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnSimpan.setPreferredSize(new java.awt.Dimension(300, 45));
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        btnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnSimpanKeyPressed(evt);
            }
        });
        jPanel3.add(btnSimpan);

        btnFingerPrint.setForeground(new java.awt.Color(0, 131, 62));
        btnFingerPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/fingerprint.png"))); // NOI18N
        btnFingerPrint.setMnemonic('K');
        btnFingerPrint.setText("FINGERPRINT BPJS");
        btnFingerPrint.setToolTipText("Alt+K");
        btnFingerPrint.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        btnFingerPrint.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnFingerPrint.setPreferredSize(new java.awt.Dimension(300, 45));
        btnFingerPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFingerPrintActionPerformed(evt);
            }
        });
        btnFingerPrint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnFingerPrintKeyPressed(evt);
            }
        });
        jPanel3.add(btnFingerPrint);

        btnKeluar.setForeground(new java.awt.Color(0, 131, 62));
        btnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/reset.png"))); // NOI18N
        btnKeluar.setMnemonic('K');
        btnKeluar.setText("Batal");
        btnKeluar.setToolTipText("Alt+K");
        btnKeluar.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        btnKeluar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnKeluar.setPreferredSize(new java.awt.Dimension(300, 45));
        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });
        btnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnKeluarKeyPressed(evt);
            }
        });
        jPanel3.add(btnKeluar);

        jPanel1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        internalFrame2.setBorder(null);
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));

        scrollInput.setBackground(new java.awt.Color(238, 238, 255));
        scrollInput.setPreferredSize(new java.awt.Dimension(1300, 800));

        FormInput.setBackground(new java.awt.Color(238, 238, 255));
        FormInput.setBorder(null);
        FormInput.setForeground(new java.awt.Color(0, 131, 62));
        FormInput.setToolTipText("");
        FormInput.setPreferredSize(new java.awt.Dimension(1300, 620));
        FormInput.setLayout(null);

        TPasien.setBackground(new java.awt.Color(245, 250, 240));
        TPasien.setHighlighter(null);
        FormInput.add(TPasien);
        TPasien.setBounds(340, 10, 230, 30);

        TNoRM.setHighlighter(null);
        TNoRM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TNoRMActionPerformed(evt);
            }
        });
        FormInput.add(TNoRM);
        TNoRM.setBounds(230, 10, 110, 30);

        NoKartu.setEditable(false);
        NoKartu.setBackground(new java.awt.Color(255, 255, 153));
        NoKartu.setHighlighter(null);
        FormInput.add(NoKartu);
        NoKartu.setBounds(730, 70, 300, 30);

        jLabel20.setForeground(new java.awt.Color(0, 131, 62));
        jLabel20.setText("Tgl.SEP :");
        jLabel20.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel20.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel20);
        jLabel20.setBounds(660, 130, 70, 30);

        TanggalSEP.setForeground(new java.awt.Color(50, 70, 50));
        TanggalSEP.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "31-07-2023" }));
        TanggalSEP.setDisplayFormat("dd-MM-yyyy");
        TanggalSEP.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        TanggalSEP.setOpaque(false);
        TanggalSEP.setPreferredSize(new java.awt.Dimension(95, 25));
        TanggalSEP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalSEPKeyPressed(evt);
            }
        });
        FormInput.add(TanggalSEP);
        TanggalSEP.setBounds(730, 130, 170, 30);

        jLabel22.setForeground(new java.awt.Color(0, 131, 62));
        jLabel22.setText("Tgl.Rujuk :");
        jLabel22.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel22.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel22);
        jLabel22.setBounds(650, 160, 80, 30);

        TanggalRujuk.setForeground(new java.awt.Color(50, 70, 50));
        TanggalRujuk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "31-07-2023" }));
        TanggalRujuk.setDisplayFormat("dd-MM-yyyy");
        TanggalRujuk.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        TanggalRujuk.setOpaque(false);
        TanggalRujuk.setPreferredSize(new java.awt.Dimension(95, 23));
        TanggalRujuk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalRujukKeyPressed(evt);
            }
        });
        FormInput.add(TanggalRujuk);
        TanggalRujuk.setBounds(730, 160, 170, 30);

        jLabel23.setForeground(new java.awt.Color(0, 131, 62));
        jLabel23.setText("No.SKDP / S. Kontrol :");
        jLabel23.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel23.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel23);
        jLabel23.setBounds(90, 70, 140, 30);

        NoRujukan.setEditable(false);
        NoRujukan.setBackground(new java.awt.Color(255, 255, 153));
        NoRujukan.setHighlighter(null);
        NoRujukan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoRujukanKeyPressed(evt);
            }
        });
        FormInput.add(NoRujukan);
        NoRujukan.setBounds(230, 100, 340, 30);

        jLabel9.setForeground(new java.awt.Color(0, 131, 62));
        jLabel9.setText("PPK Pelayanan :");
        jLabel9.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel9.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel9);
        jLabel9.setBounds(80, 250, 150, 30);

        KdPPK.setEditable(false);
        KdPPK.setBackground(new java.awt.Color(245, 250, 240));
        KdPPK.setHighlighter(null);
        FormInput.add(KdPPK);
        KdPPK.setBounds(230, 250, 75, 30);

        NmPPK.setEditable(false);
        NmPPK.setBackground(new java.awt.Color(245, 250, 240));
        NmPPK.setHighlighter(null);
        FormInput.add(NmPPK);
        NmPPK.setBounds(310, 250, 260, 30);

        jLabel10.setForeground(new java.awt.Color(0, 131, 62));
        jLabel10.setText("PPK Rujukan :");
        jLabel10.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel10);
        jLabel10.setBounds(110, 130, 120, 30);

        KdPpkRujukan.setEditable(false);
        KdPpkRujukan.setBackground(new java.awt.Color(245, 250, 240));
        KdPpkRujukan.setHighlighter(null);
        FormInput.add(KdPpkRujukan);
        KdPpkRujukan.setBounds(230, 130, 75, 30);

        NmPpkRujukan.setEditable(false);
        NmPpkRujukan.setBackground(new java.awt.Color(245, 250, 240));
        NmPpkRujukan.setHighlighter(null);
        FormInput.add(NmPpkRujukan);
        NmPpkRujukan.setBounds(310, 130, 260, 30);

        jLabel11.setForeground(new java.awt.Color(0, 131, 62));
        jLabel11.setText("Diagnosa Awal :");
        jLabel11.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel11);
        jLabel11.setBounds(90, 160, 140, 30);

        KdPenyakit.setEditable(false);
        KdPenyakit.setBackground(new java.awt.Color(255, 255, 153));
        KdPenyakit.setHighlighter(null);
        FormInput.add(KdPenyakit);
        KdPenyakit.setBounds(230, 160, 75, 30);

        NmPenyakit.setEditable(false);
        NmPenyakit.setBackground(new java.awt.Color(255, 255, 153));
        NmPenyakit.setHighlighter(null);
        FormInput.add(NmPenyakit);
        NmPenyakit.setBounds(310, 160, 260, 30);

        NmPoli.setEditable(false);
        NmPoli.setBackground(new java.awt.Color(255, 255, 153));
        NmPoli.setHighlighter(null);
        FormInput.add(NmPoli);
        NmPoli.setBounds(310, 190, 260, 30);

        KdPoli.setEditable(false);
        KdPoli.setBackground(new java.awt.Color(255, 255, 153));
        KdPoli.setHighlighter(null);
        FormInput.add(KdPoli);
        KdPoli.setBounds(230, 190, 75, 30);

        LabelPoli.setForeground(new java.awt.Color(0, 131, 62));
        LabelPoli.setText("Poli Tujuan :");
        LabelPoli.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        LabelPoli.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(LabelPoli);
        LabelPoli.setBounds(120, 190, 110, 30);

        jLabel13.setForeground(new java.awt.Color(0, 131, 62));
        jLabel13.setText("Jns.Pelayanan :");
        jLabel13.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel13.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel13);
        jLabel13.setBounds(90, 280, 140, 30);

        jLabel14.setForeground(new java.awt.Color(0, 131, 62));
        jLabel14.setText("Catatan :");
        jLabel14.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(640, 460, 90, 30);

        Catatan.setHighlighter(null);
        Catatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CatatanKeyPressed(evt);
            }
        });
        FormInput.add(Catatan);
        Catatan.setBounds(730, 460, 300, 30);

        JenisPelayanan.setBackground(new java.awt.Color(255, 255, 153));
        JenisPelayanan.setForeground(new java.awt.Color(0, 131, 62));
        JenisPelayanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Ranap", "2. Ralan" }));
        JenisPelayanan.setSelectedIndex(1);
        JenisPelayanan.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        JenisPelayanan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                JenisPelayananItemStateChanged(evt);
            }
        });
        JenisPelayanan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JenisPelayananKeyPressed(evt);
            }
        });
        FormInput.add(JenisPelayanan);
        JenisPelayanan.setBounds(230, 280, 110, 30);

        LabelKelas.setForeground(new java.awt.Color(0, 131, 62));
        LabelKelas.setText("Kelas :");
        LabelKelas.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        FormInput.add(LabelKelas);
        LabelKelas.setBounds(350, 280, 50, 30);

        Kelas.setForeground(new java.awt.Color(0, 131, 62));
        Kelas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Kelas 1", "2. Kelas 2", "3. Kelas 3" }));
        Kelas.setSelectedIndex(2);
        Kelas.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        Kelas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KelasKeyPressed(evt);
            }
        });
        FormInput.add(Kelas);
        Kelas.setBounds(400, 280, 100, 30);

        LakaLantas.setForeground(new java.awt.Color(0, 131, 62));
        LakaLantas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Bukan KLL", "1. KLL Bukan KK", "2. KLL dan KK", "3. KK" }));
        LakaLantas.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        LakaLantas.setPreferredSize(new java.awt.Dimension(64, 25));
        LakaLantas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LakaLantasItemStateChanged(evt);
            }
        });
        LakaLantas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LakaLantasKeyPressed(evt);
            }
        });
        FormInput.add(LakaLantas);
        LakaLantas.setBounds(730, 250, 160, 30);

        jLabel8.setForeground(new java.awt.Color(0, 131, 62));
        jLabel8.setText("Data Pasien : ");
        jLabel8.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel8);
        jLabel8.setBounds(90, 10, 140, 30);

        TglLahir.setEditable(false);
        TglLahir.setBackground(new java.awt.Color(245, 250, 240));
        TglLahir.setHighlighter(null);
        FormInput.add(TglLahir);
        TglLahir.setBounds(230, 40, 110, 30);

        jLabel18.setForeground(new java.awt.Color(0, 131, 62));
        jLabel18.setText("J.K :");
        jLabel18.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(910, 10, 30, 30);

        JK.setEditable(false);
        JK.setBackground(new java.awt.Color(245, 250, 240));
        JK.setHighlighter(null);
        FormInput.add(JK);
        JK.setBounds(940, 10, 90, 30);

        jLabel24.setForeground(new java.awt.Color(0, 131, 62));
        jLabel24.setText("Peserta :");
        jLabel24.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel24.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel24);
        jLabel24.setBounds(670, 10, 60, 30);

        JenisPeserta.setEditable(false);
        JenisPeserta.setBackground(new java.awt.Color(245, 250, 240));
        JenisPeserta.setHighlighter(null);
        FormInput.add(JenisPeserta);
        JenisPeserta.setBounds(730, 10, 173, 30);

        jLabel25.setForeground(new java.awt.Color(0, 131, 62));
        jLabel25.setText("Status :");
        jLabel25.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel25.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel25);
        jLabel25.setBounds(390, 40, 50, 30);

        Status.setEditable(false);
        Status.setBackground(new java.awt.Color(245, 250, 240));
        Status.setHighlighter(null);
        FormInput.add(Status);
        Status.setBounds(440, 40, 130, 30);

        jLabel27.setForeground(new java.awt.Color(0, 131, 62));
        jLabel27.setText("Asal Rujukan :");
        jLabel27.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        FormInput.add(jLabel27);
        jLabel27.setBounds(630, 100, 100, 30);

        AsalRujukan.setForeground(new java.awt.Color(0, 131, 62));
        AsalRujukan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Faskes 1", "2. Faskes 2(RS)" }));
        AsalRujukan.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        AsalRujukan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AsalRujukanKeyPressed(evt);
            }
        });
        FormInput.add(AsalRujukan);
        AsalRujukan.setBounds(730, 100, 170, 30);

        NoTelp.setHighlighter(null);
        NoTelp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoTelpKeyPressed(evt);
            }
        });
        FormInput.add(NoTelp);
        NoTelp.setBounds(730, 190, 160, 30);

        Katarak.setForeground(new java.awt.Color(0, 131, 62));
        Katarak.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        Katarak.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        Katarak.setPreferredSize(new java.awt.Dimension(64, 25));
        Katarak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KatarakKeyPressed(evt);
            }
        });
        FormInput.add(Katarak);
        Katarak.setBounds(730, 220, 160, 30);

        jLabel37.setForeground(new java.awt.Color(0, 131, 62));
        jLabel37.setText("Katarak :");
        jLabel37.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        FormInput.add(jLabel37);
        jLabel37.setBounds(640, 220, 87, 30);

        jLabel38.setForeground(new java.awt.Color(0, 131, 62));
        jLabel38.setText("Tgl KLL :");
        jLabel38.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel38.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel38);
        jLabel38.setBounds(650, 280, 80, 30);

        TanggalKKL.setForeground(new java.awt.Color(50, 70, 50));
        TanggalKKL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "31-07-2023" }));
        TanggalKKL.setDisplayFormat("dd-MM-yyyy");
        TanggalKKL.setEnabled(false);
        TanggalKKL.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        TanggalKKL.setOpaque(false);
        TanggalKKL.setPreferredSize(new java.awt.Dimension(64, 25));
        TanggalKKL.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKKLKeyPressed(evt);
            }
        });
        FormInput.add(TanggalKKL);
        TanggalKKL.setBounds(730, 280, 140, 30);

        LabelPoli2.setForeground(new java.awt.Color(0, 131, 62));
        LabelPoli2.setText("Dokter DPJP :");
        LabelPoli2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        LabelPoli2.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(LabelPoli2);
        LabelPoli2.setBounds(120, 220, 110, 30);

        KdDPJP.setEditable(false);
        KdDPJP.setBackground(new java.awt.Color(255, 255, 153));
        KdDPJP.setHighlighter(null);
        FormInput.add(KdDPJP);
        KdDPJP.setBounds(230, 220, 75, 30);

        NmDPJP.setEditable(false);
        NmDPJP.setBackground(new java.awt.Color(255, 255, 153));
        NmDPJP.setHighlighter(null);
        FormInput.add(NmDPJP);
        NmDPJP.setBounds(310, 220, 260, 30);

        jLabel36.setForeground(new java.awt.Color(0, 131, 62));
        jLabel36.setText("Keterangan :");
        jLabel36.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel36.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel36);
        jLabel36.setBounds(640, 310, 87, 30);

        Keterangan.setEditable(false);
        Keterangan.setHighlighter(null);
        Keterangan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeteranganKeyPressed(evt);
            }
        });
        FormInput.add(Keterangan);
        Keterangan.setBounds(730, 310, 257, 30);

        jLabel40.setForeground(new java.awt.Color(0, 131, 62));
        jLabel40.setText("Suplesi :");
        jLabel40.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        FormInput.add(jLabel40);
        jLabel40.setBounds(640, 340, 87, 30);

        Suplesi.setForeground(new java.awt.Color(0, 131, 62));
        Suplesi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Tidak", "1.Ya" }));
        Suplesi.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        Suplesi.setPreferredSize(new java.awt.Dimension(64, 25));
        Suplesi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SuplesiKeyPressed(evt);
            }
        });
        FormInput.add(Suplesi);
        Suplesi.setBounds(730, 340, 90, 30);

        NoSEPSuplesi.setHighlighter(null);
        NoSEPSuplesi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoSEPSuplesiKeyPressed(evt);
            }
        });
        FormInput.add(NoSEPSuplesi);
        NoSEPSuplesi.setBounds(890, 340, 140, 30);

        jLabel41.setForeground(new java.awt.Color(0, 131, 62));
        jLabel41.setText("Suplesi :");
        jLabel41.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel41.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel41);
        jLabel41.setBounds(820, 340, 68, 30);

        LabelPoli3.setForeground(new java.awt.Color(0, 131, 62));
        LabelPoli3.setText("Propinsi KLL :");
        LabelPoli3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        FormInput.add(LabelPoli3);
        LabelPoli3.setBounds(640, 370, 87, 30);

        KdPropinsi.setEditable(false);
        KdPropinsi.setBackground(new java.awt.Color(245, 250, 240));
        KdPropinsi.setHighlighter(null);
        FormInput.add(KdPropinsi);
        KdPropinsi.setBounds(730, 370, 55, 30);

        NmPropinsi.setEditable(false);
        NmPropinsi.setBackground(new java.awt.Color(245, 250, 240));
        NmPropinsi.setHighlighter(null);
        FormInput.add(NmPropinsi);
        NmPropinsi.setBounds(790, 370, 240, 30);

        LabelPoli4.setForeground(new java.awt.Color(0, 131, 62));
        LabelPoli4.setText("Kabupaten KLL :");
        LabelPoli4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        FormInput.add(LabelPoli4);
        LabelPoli4.setBounds(620, 400, 110, 30);

        KdKabupaten.setEditable(false);
        KdKabupaten.setBackground(new java.awt.Color(245, 250, 240));
        KdKabupaten.setHighlighter(null);
        FormInput.add(KdKabupaten);
        KdKabupaten.setBounds(730, 400, 55, 30);

        NmKabupaten.setEditable(false);
        NmKabupaten.setBackground(new java.awt.Color(245, 250, 240));
        NmKabupaten.setHighlighter(null);
        FormInput.add(NmKabupaten);
        NmKabupaten.setBounds(790, 400, 240, 30);

        LabelPoli5.setForeground(new java.awt.Color(0, 131, 62));
        LabelPoli5.setText("Kecamatan KLL :");
        LabelPoli5.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        FormInput.add(LabelPoli5);
        LabelPoli5.setBounds(610, 430, 120, 30);

        KdKecamatan.setEditable(false);
        KdKecamatan.setBackground(new java.awt.Color(245, 250, 240));
        KdKecamatan.setHighlighter(null);
        FormInput.add(KdKecamatan);
        KdKecamatan.setBounds(730, 430, 55, 30);

        NmKecamatan.setEditable(false);
        NmKecamatan.setBackground(new java.awt.Color(245, 250, 240));
        NmKecamatan.setHighlighter(null);
        FormInput.add(NmKecamatan);
        NmKecamatan.setBounds(790, 430, 240, 30);

        jLabel42.setForeground(new java.awt.Color(0, 131, 62));
        jLabel42.setText("Tujuan Kunjungan :");
        jLabel42.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel42.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel42);
        jLabel42.setBounds(90, 310, 140, 30);

        TujuanKunjungan.setBackground(new java.awt.Color(255, 255, 153));
        TujuanKunjungan.setForeground(new java.awt.Color(0, 131, 62));
        TujuanKunjungan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Normal", "1. Prosedur", "2. Konsul Dokter" }));
        TujuanKunjungan.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        TujuanKunjungan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                TujuanKunjunganItemStateChanged(evt);
            }
        });
        TujuanKunjungan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TujuanKunjunganKeyPressed(evt);
            }
        });
        FormInput.add(TujuanKunjungan);
        TujuanKunjungan.setBounds(230, 310, 340, 30);

        FlagProsedur.setForeground(new java.awt.Color(0, 131, 62));
        FlagProsedur.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "0. Prosedur Tidak Berkelanjutan", "1. Prosedur dan Terapi Berkelanjutan" }));
        FlagProsedur.setEnabled(false);
        FlagProsedur.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        FlagProsedur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                FlagProsedurKeyPressed(evt);
            }
        });
        FormInput.add(FlagProsedur);
        FlagProsedur.setBounds(230, 340, 340, 30);

        jLabel43.setForeground(new java.awt.Color(0, 131, 62));
        jLabel43.setText("Flag Prosedur :");
        jLabel43.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel43.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel43);
        jLabel43.setBounds(90, 340, 140, 30);

        jLabel44.setForeground(new java.awt.Color(0, 131, 62));
        jLabel44.setText("Penunjang :");
        jLabel44.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel44.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel44);
        jLabel44.setBounds(90, 370, 140, 30);

        Penunjang.setForeground(new java.awt.Color(0, 131, 62));
        Penunjang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Radioterapi", "2. Kemoterapi", "3. Rehabilitasi Medik", "4. Rehabilitasi Psikososial", "5. Transfusi Darah", "6. Pelayanan Gigi", "7. Laboratorium", "8. USG", "9. Farmasi", "10. Lain-Lain", "11. MRI", "12. HEMODIALISA" }));
        Penunjang.setEnabled(false);
        Penunjang.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        Penunjang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PenunjangKeyPressed(evt);
            }
        });
        FormInput.add(Penunjang);
        Penunjang.setBounds(230, 370, 340, 30);

        jLabel45.setForeground(new java.awt.Color(0, 131, 62));
        jLabel45.setText("Asesmen Pelayanan :");
        jLabel45.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel45.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel45);
        jLabel45.setBounds(90, 400, 140, 30);

        AsesmenPoli.setBackground(new java.awt.Color(255, 255, 153));
        AsesmenPoli.setForeground(new java.awt.Color(0, 131, 62));
        AsesmenPoli.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1. Poli spesialis tidak tersedia pada hari sebelumnya", "2. Jam Poli telah berakhir pada hari sebelumnya", "3. Spesialis yang dimaksud tidak praktek pada hari sebelumnya", "4. Atas Instruksi RS", "5. Tujuan Kontrol" }));
        AsesmenPoli.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        AsesmenPoli.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AsesmenPoliKeyPressed(evt);
            }
        });
        FormInput.add(AsesmenPoli);
        AsesmenPoli.setBounds(230, 400, 340, 30);

        LabelPoli6.setForeground(new java.awt.Color(0, 131, 62));
        LabelPoli6.setText("DPJP Layanan :");
        LabelPoli6.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        LabelPoli6.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(LabelPoli6);
        LabelPoli6.setBounds(90, 430, 140, 30);

        KdDPJPLayanan.setEditable(false);
        KdDPJPLayanan.setBackground(new java.awt.Color(255, 255, 153));
        KdDPJPLayanan.setHighlighter(null);
        FormInput.add(KdDPJPLayanan);
        KdDPJPLayanan.setBounds(230, 430, 80, 30);

        NmDPJPLayanan.setEditable(false);
        NmDPJPLayanan.setBackground(new java.awt.Color(255, 255, 153));
        NmDPJPLayanan.setHighlighter(null);
        FormInput.add(NmDPJPLayanan);
        NmDPJPLayanan.setBounds(310, 430, 260, 30);

        btnDPJPLayanan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnDPJPLayanan.setMnemonic('X');
        btnDPJPLayanan.setToolTipText("Alt+X");
        btnDPJPLayanan.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnDPJPLayanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDPJPLayananActionPerformed(evt);
            }
        });
        btnDPJPLayanan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnDPJPLayananKeyPressed(evt);
            }
        });
        FormInput.add(btnDPJPLayanan);
        btnDPJPLayanan.setBounds(570, 220, 40, 30);

        jLabel55.setForeground(new java.awt.Color(0, 131, 62));
        jLabel55.setText("Laka Lantas :");
        jLabel55.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        FormInput.add(jLabel55);
        jLabel55.setBounds(640, 250, 87, 30);

        jLabel56.setForeground(new java.awt.Color(0, 131, 62));
        jLabel56.setText("No.Telp :");
        jLabel56.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel56.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel56);
        jLabel56.setBounds(670, 190, 58, 30);

        jLabel12.setForeground(new java.awt.Color(0, 131, 62));
        jLabel12.setText("Tgl.Lahir :");
        jLabel12.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel12.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel12);
        jLabel12.setBounds(120, 40, 110, 30);

        jLabel6.setForeground(new java.awt.Color(0, 131, 62));
        jLabel6.setText("NIK :");
        jLabel6.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        FormInput.add(jLabel6);
        jLabel6.setBounds(650, 40, 80, 30);

        NoSKDP.setBackground(new java.awt.Color(255, 255, 153));
        NoSKDP.setHighlighter(null);
        NoSKDP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoSKDPKeyPressed(evt);
            }
        });
        FormInput.add(NoSKDP);
        NoSKDP.setBounds(230, 70, 340, 30);

        jLabel26.setForeground(new java.awt.Color(0, 131, 62));
        jLabel26.setText("No.Rujukan :");
        jLabel26.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel26.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel26);
        jLabel26.setBounds(130, 100, 100, 30);

        NIK.setEditable(false);
        NIK.setBackground(new java.awt.Color(255, 255, 153));
        NIK.setHighlighter(null);
        FormInput.add(NIK);
        NIK.setBounds(730, 40, 300, 30);

        jLabel7.setForeground(new java.awt.Color(0, 131, 62));
        jLabel7.setText("No.Kartu :");
        jLabel7.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        FormInput.add(jLabel7);
        jLabel7.setBounds(650, 70, 80, 30);

        btnDPJPLayanan1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnDPJPLayanan1.setMnemonic('X');
        btnDPJPLayanan1.setToolTipText("Alt+X");
        btnDPJPLayanan1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnDPJPLayanan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDPJPLayanan1ActionPerformed(evt);
            }
        });
        btnDPJPLayanan1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnDPJPLayanan1KeyPressed(evt);
            }
        });
        FormInput.add(btnDPJPLayanan1);
        btnDPJPLayanan1.setBounds(570, 190, 40, 30);

        btnDiagnosaAwal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnDiagnosaAwal.setMnemonic('X');
        btnDiagnosaAwal.setToolTipText("Alt+X");
        btnDiagnosaAwal.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnDiagnosaAwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiagnosaAwalActionPerformed(evt);
            }
        });
        btnDiagnosaAwal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnDiagnosaAwalKeyPressed(evt);
            }
        });
        FormInput.add(btnDiagnosaAwal);
        btnDiagnosaAwal.setBounds(570, 160, 40, 30);

        btnDiagnosaAwal1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnDiagnosaAwal1.setMnemonic('X');
        btnDiagnosaAwal1.setToolTipText("Alt+X");
        btnDiagnosaAwal1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnDiagnosaAwal1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiagnosaAwal1ActionPerformed(evt);
            }
        });
        btnDiagnosaAwal1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnDiagnosaAwal1KeyPressed(evt);
            }
        });
        FormInput.add(btnDiagnosaAwal1);
        btnDiagnosaAwal1.setBounds(570, 100, 40, 30);

        btnDiagnosaAwal2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/pilih.png"))); // NOI18N
        btnDiagnosaAwal2.setMnemonic('X');
        btnDiagnosaAwal2.setText("Riwayat Layanan BPJS");
        btnDiagnosaAwal2.setToolTipText("Alt+X");
        btnDiagnosaAwal2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnDiagnosaAwal2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiagnosaAwal2ActionPerformed(evt);
            }
        });
        btnDiagnosaAwal2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnDiagnosaAwal2KeyPressed(evt);
            }
        });
        FormInput.add(btnDiagnosaAwal2);
        btnDiagnosaAwal2.setBounds(1040, 160, 220, 30);

        scrollInput.setViewportView(FormInput);

        internalFrame2.add(scrollInput, java.awt.BorderLayout.CENTER);

        jPanel1.add(internalFrame2, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened

    }//GEN-LAST:event_formWindowOpened

    private void NoRegActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NoRegActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoRegActionPerformed

    private void NoRegKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRegKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoRegKeyPressed

    private void NoRawatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NoRawatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoRawatActionPerformed

    private void NoRawatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRawatKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoRawatKeyPressed

    private void BiayaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BiayaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BiayaActionPerformed

    private void BiayaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BiayaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BiayaKeyPressed

    private void btnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnKeluarActionPerformed(null);
        }
    }//GEN-LAST:event_btnKeluarKeyPressed

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_btnKeluarActionPerformed

    private void btnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnSimpanActionPerformed(null);
        }
    }//GEN-LAST:event_btnSimpanKeyPressed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        cekFinger(NoKartu.getText());
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "Pasien");
        } else if (NoKartu.getText().trim().equals("")) {
            Valid.textKosong(NoKartu, "Nomor Kartu");
        } else if (KdPpkRujukan.getText().trim().equals("") || NmPpkRujukan.getText().trim().equals("")) {
            Valid.textKosong(KdPpkRujukan, "PPK Rujukan");
        } else if (KdPPK.getText().trim().equals("") || NmPPK.getText().trim().equals("")) {
            Valid.textKosong(KdPPK, "PPK Pelayanan");
        } else if (KdPenyakit.getText().trim().equals("") || NmPenyakit.getText().trim().equals("")) {
            Valid.textKosong(KdPenyakit, "Diagnosa");
        } else if (Catatan.getText().trim().equals("")) {
            Valid.textKosong(Catatan, "Catatan");
        } else if ((JenisPelayanan.getSelectedIndex() == 1) && (KdPoli.getText().trim().equals("") || NmPoli.getText().trim().equals(""))) {
            Valid.textKosong(KdPoli, "Poli Tujuan");
        } else if ((LakaLantas.getSelectedIndex() == 1) && Keterangan.getText().equals("")) {
            Valid.textKosong(Keterangan, "Keterangan");
        } else if (KdDPJP.getText().trim().equals("") || NmDPJP.getText().trim().equals("")) {
            Valid.textKosong(KdDPJP, "DPJP");
        } else if (statusfinger == false && Sequel.cariInteger("select timestampdiff(year, '" + TglLahir.getText() + "', CURRENT_DATE())") >= 17 && JenisPelayanan.getSelectedIndex() != 0 && !KdPoli.getText().equals("IGD")) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, Pasien belum melakukan Fingerprint");
            BukaFingerPrint(NoKartu.getText());
        } else {
            kodepolireg = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", KdPoli.getText());
            kodedokterreg = Sequel.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", KdDPJP.getText());
            if (!kodepolireg.equals("")) {
                isPoli();
            } else {
                isPoli();
            }
            isCekPasien();
            isNumber();
            if (JenisPelayanan.getSelectedIndex() == 0) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                insertSEP();
                // simpan registrasi pasien
//                SimpanRegistrasi();
                // buat SEP
//                insertSEP2();
                
                this.setCursor(Cursor.getDefaultCursor());
            } else if (JenisPelayanan.getSelectedIndex() == 1) {
                if (NmPoli.getText().toLowerCase().contains("darurat")) {
                    if (Sequel.cariInteger("select count(bridging_sep.no_kartu) from bridging_sep where bridging_sep.no_kartu='" + no_peserta + "' and bridging_sep.jnspelayanan='" + JenisPelayanan.getSelectedItem().toString().substring(0, 1) + "' and bridging_sep.tglsep like '%" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "%' and bridging_sep.nmpolitujuan like '%darurat%'") >= 3) {
                        JOptionPane.showMessageDialog(rootPane, "Maaf, sebelumnya sudah dilakukan 3x pembuatan SEP di jenis pelayanan yang sama..!!");
                    } else {
                        //cek apakah no rawat sudah digunakan
                        if(Sequel.cariInteger("select count(*) from reg_periksa where no_rawat = '"+TNoRw.getText()+"'") > 0){
                            isNumber();
                        }
                        if ((!kodedokterreg.equals("")) && (!kodepolireg.equals(""))) {
                            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                            SimpanAntrianOnSite();
                            this.setCursor(Cursor.getDefaultCursor());
                        }
                        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        insertSEP();
                        // simpan registrasi pasien
//                        SimpanRegistrasi();
                        // buat SEP
//                        insertSEP2();
                        this.setCursor(Cursor.getDefaultCursor());
                    }
                } else if (!NmPoli.getText().toLowerCase().contains("darurat")) {
                    TulisLog("===========================+++++++++++++++++++++++++++++++==================================");
                    TulisLog("Mulai proses simpan "+TNoRM.getText());
                    if (Sequel.cariInteger("select count(bridging_sep.no_kartu) from bridging_sep where bridging_sep.no_kartu='" + no_peserta + "' and bridging_sep.jnspelayanan='" + JenisPelayanan.getSelectedItem().toString().substring(0, 1) + "' and bridging_sep.tglsep like '%" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "%' and bridging_sep.nmpolitujuan not like '%darurat%'") >= 1) {
                        JOptionPane.showMessageDialog(rootPane, "Maaf, sebelumnya sudah dilakukan pembuatan SEP di jenis pelayanan yang sama..!!");
                    } else {
                        //cek apakah no rawat sudah digunakan
                        if(Sequel.cariInteger("select count(*) from reg_periksa where no_rawat = '"+TNoRw.getText()+"'") > 0){
                            isNumber();
                        }
                        if ((!kodedokterreg.equals("")) && (!kodepolireg.equals(""))) {
                            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                            TulisLog("Dokter & Poli ada isinya.. Checkpoint sebelum panggil");
                            SimpanAntrianOnSite();
                            this.setCursor(Cursor.getDefaultCursor());
                        }
                        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        insertSEP();
                        // simpan registrasi pasien
//                        SimpanRegistrasi();
                        // buat SEP
//                        insertSEP2();
                        this.setCursor(Cursor.getDefaultCursor());
                        TulisLog("Proses Selesai.\n\n");
                    }
                }
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnDPJPLayananKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnDPJPLayananKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDPJPLayananKeyPressed

    private void btnDPJPLayananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDPJPLayananActionPerformed
        dokter.setSize(jPanel1.getWidth() - 75, jPanel1.getHeight() - 75);
//        dokter.setLocationRelativeTo(jPanel1);
        dokter.setLocation(10, 10);
        dokter.carinamadokter(KdPoli.getText(), NmPoli.getText());
        dokter.setVisible(true);
    }//GEN-LAST:event_btnDPJPLayananActionPerformed

    private void AsesmenPoliKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AsesmenPoliKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_AsesmenPoliKeyPressed

    private void PenunjangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PenunjangKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PenunjangKeyPressed

    private void FlagProsedurKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_FlagProsedurKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_FlagProsedurKeyPressed

    private void TujuanKunjunganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TujuanKunjunganKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TujuanKunjunganKeyPressed

    private void TujuanKunjunganItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_TujuanKunjunganItemStateChanged
        if (TujuanKunjungan.getSelectedIndex() == 0) {
            FlagProsedur.setEnabled(false);
            FlagProsedur.setSelectedIndex(0);
            Penunjang.setEnabled(false);
            Penunjang.setSelectedIndex(0);
            AsesmenPoli.setEnabled(true);
        } else {
            if (TujuanKunjungan.getSelectedIndex() == 1) {
                AsesmenPoli.setSelectedIndex(0);
                AsesmenPoli.setEnabled(false);
            } else {
                AsesmenPoli.setEnabled(true);
            }
            if (FlagProsedur.getSelectedIndex() == 0) {
                FlagProsedur.setSelectedIndex(2);
            }
            FlagProsedur.setEnabled(true);
            if (Penunjang.getSelectedIndex() == 0) {
                Penunjang.setSelectedIndex(10);
            }
            Penunjang.setEnabled(true);
        }
    }//GEN-LAST:event_TujuanKunjunganItemStateChanged

    private void NoSEPSuplesiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoSEPSuplesiKeyPressed

    }//GEN-LAST:event_NoSEPSuplesiKeyPressed

    private void SuplesiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SuplesiKeyPressed
        Valid.pindah(evt, Keterangan, NoSEPSuplesi);
    }//GEN-LAST:event_SuplesiKeyPressed

    private void KeteranganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KeteranganKeyPressed
        Valid.pindah(evt, TanggalKKL, Suplesi);
    }//GEN-LAST:event_KeteranganKeyPressed

    private void TanggalKKLKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalKKLKeyPressed
        Valid.pindah(evt, LakaLantas, Keterangan);
    }//GEN-LAST:event_TanggalKKLKeyPressed

    private void KatarakKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KatarakKeyPressed
        Valid.pindah(evt, Catatan, NoTelp);
    }//GEN-LAST:event_KatarakKeyPressed

    private void NoTelpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoTelpKeyPressed
        Valid.pindah(evt, Katarak, LakaLantas);
    }//GEN-LAST:event_NoTelpKeyPressed

    private void AsalRujukanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AsalRujukanKeyPressed

    }//GEN-LAST:event_AsalRujukanKeyPressed

    private void LakaLantasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LakaLantasKeyPressed
        Valid.pindah(evt, NoTelp, TanggalKKL);
    }//GEN-LAST:event_LakaLantasKeyPressed

    private void LakaLantasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LakaLantasItemStateChanged
        if (LakaLantas.getSelectedIndex() == 0) {
            TanggalKKL.setEnabled(false);
            Keterangan.setEditable(false);
            Keterangan.setText("");
        } else {
            TanggalKKL.setEnabled(true);
            Keterangan.setEditable(true);
        }
    }//GEN-LAST:event_LakaLantasItemStateChanged

    private void KelasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KelasKeyPressed

    }//GEN-LAST:event_KelasKeyPressed

    private void JenisPelayananKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JenisPelayananKeyPressed

    }//GEN-LAST:event_JenisPelayananKeyPressed

    private void JenisPelayananItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_JenisPelayananItemStateChanged
        if (JenisPelayanan.getSelectedIndex() == 0) {
            KdPoli.setText("");
            NmPoli.setText("");
            LabelPoli.setVisible(false);
            KdPoli.setVisible(false);
            NmPoli.setVisible(false);

            KdDPJPLayanan.setText("");
            NmDPJPLayanan.setText("");
            btnDPJPLayanan.setEnabled(false);
        } else if (JenisPelayanan.getSelectedIndex() == 1) {
            LabelPoli.setVisible(true);
            KdPoli.setVisible(true);
            NmPoli.setVisible(true);

            btnDPJPLayanan.setEnabled(true);
        }
    }//GEN-LAST:event_JenisPelayananItemStateChanged

    private void CatatanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CatatanKeyPressed

    }//GEN-LAST:event_CatatanKeyPressed

    private void NoRujukanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRujukanKeyPressed

    }//GEN-LAST:event_NoRujukanKeyPressed

    private void TanggalRujukKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalRujukKeyPressed
        Valid.pindah(evt, NoRujukan, TanggalSEP);
    }//GEN-LAST:event_TanggalRujukKeyPressed

    private void TanggalSEPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalSEPKeyPressed
        Valid.pindah(evt, TanggalRujuk, AsalRujukan);
    }//GEN-LAST:event_TanggalSEPKeyPressed

    private void TNoRMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TNoRMActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TNoRMActionPerformed

    private void kdpoliKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdpoliKeyPressed

    }//GEN-LAST:event_kdpoliKeyPressed

    private void TBiayaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TBiayaKeyPressed

    }//GEN-LAST:event_TBiayaKeyPressed

    private void KdpnjKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdpnjKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KdpnjKeyPressed

    private void nmpnjKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nmpnjKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_nmpnjKeyPressed

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TNoRwKeyPressed

    private void NoRujukMasukKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRujukMasukKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoRujukMasukKeyPressed

    private void TanggalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TanggalKeyPressed

    private void NoSKDPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoSKDPKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoSKDPKeyPressed

    private void btnFingerPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFingerPrintActionPerformed
        if (!NoKartu.toString().equals("")) {
            BukaFingerPrint(NoKartu.getText());
        }
    }//GEN-LAST:event_btnFingerPrintActionPerformed

    private void btnFingerPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnFingerPrintKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFingerPrintKeyPressed

    private void btnDPJPLayanan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDPJPLayanan1ActionPerformed
        poli.setSize(jPanel1.getWidth() - 75, jPanel1.getHeight() - 75);
        poli.tampil();
        poli.setLocationRelativeTo(jPanel1);
        poli.setVisible(true);
    }//GEN-LAST:event_btnDPJPLayanan1ActionPerformed

    private void btnDPJPLayanan1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnDPJPLayanan1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDPJPLayanan1KeyPressed

    private void btnDiagnosaAwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiagnosaAwalActionPerformed
        penyakit.setSize(jPanel1.getWidth() - 70, jPanel1.getHeight() - 70);
        penyakit.setLocationRelativeTo(jPanel1);
        penyakit.setVisible(true);
    }//GEN-LAST:event_btnDiagnosaAwalActionPerformed

    private void btnDiagnosaAwalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnDiagnosaAwalKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDiagnosaAwalKeyPressed

    private void btnDiagnosaAwal1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiagnosaAwal1ActionPerformed
        if (NoKartu.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(rootPane, "No.Kartu masih kosong...!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            rujukanterakhir.setSize(jPanel1.getWidth() - 20, jPanel1.getHeight() - 50);
//            rujukanterakhir.setLocationRelativeTo(jPanel1);
            rujukanterakhir.setLocation(10, 10);
            rujukanterakhir.tampil(NoKartu.getText(), TPasien.getText());
            rujukanterakhir.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_btnDiagnosaAwal1ActionPerformed

    private void btnDiagnosaAwal1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnDiagnosaAwal1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDiagnosaAwal1KeyPressed

    private void btnDiagnosaAwal2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiagnosaAwal2ActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        historiPelayanan.setSize(jPanel1.getWidth() - 20, jPanel1.getHeight() - 50);
//            historiPelayanan.setLocationRelativeTo(jPanel1);
        historiPelayanan.setLocation(10, 10);
        historiPelayanan.setKartu(NoKartu.getText());
        historiPelayanan.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnDiagnosaAwal2ActionPerformed

    private void btnDiagnosaAwal2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnDiagnosaAwal2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDiagnosaAwal2KeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgRegistrasiSEPPertama dialog = new DlgRegistrasiSEPPertama(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.ComboBox AsalRujukan;
    private widget.ComboBox AsesmenPoli;
    private component.TextBox Biaya;
    private widget.TextBox Catatan;
    private widget.ComboBox FlagProsedur;
    private widget.PanelBiasa FormInput;
    private widget.TextBox JK;
    private widget.ComboBox JenisPelayanan;
    private widget.TextBox JenisPeserta;
    private widget.ComboBox Katarak;
    private widget.TextBox KdDPJP;
    private widget.TextBox KdDPJPLayanan;
    private widget.TextBox KdKabupaten;
    private widget.TextBox KdKecamatan;
    private widget.TextBox KdPPK;
    private widget.TextBox KdPenyakit;
    private widget.TextBox KdPoli;
    private widget.TextBox KdPpkRujukan;
    private widget.TextBox KdPropinsi;
    private widget.TextBox Kdpnj;
    private widget.ComboBox Kelas;
    private widget.TextBox Keterangan;
    private widget.Label LabelKelas;
    private widget.Label LabelPoli;
    private widget.Label LabelPoli2;
    private widget.Label LabelPoli3;
    private widget.Label LabelPoli4;
    private widget.Label LabelPoli5;
    private widget.Label LabelPoli6;
    private widget.ComboBox LakaLantas;
    private component.Label LblKdDokter;
    private component.Label LblKdPoli;
    private widget.TextBox NIK;
    private widget.TextBox NmDPJP;
    private widget.TextBox NmDPJPLayanan;
    private widget.TextBox NmKabupaten;
    private widget.TextBox NmKecamatan;
    private widget.TextBox NmPPK;
    private widget.TextBox NmPenyakit;
    private widget.TextBox NmPoli;
    private widget.TextBox NmPpkRujukan;
    private widget.TextBox NmPropinsi;
    private widget.TextBox NoKartu;
    private component.TextBox NoRawat;
    private component.TextBox NoReg;
    private widget.TextBox NoRujukMasuk;
    private widget.TextBox NoRujukan;
    private widget.TextBox NoSEPSuplesi;
    private widget.TextBox NoSKDP;
    private widget.TextBox NoTelp;
    private component.Label NoTelpPasien;
    private widget.ComboBox Penunjang;
    private widget.TextBox Status;
    private widget.ComboBox Suplesi;
    private component.Label TAlmt;
    private widget.TextBox TBiaya;
    private component.Label THbngn;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private component.Label TPngJwb;
    private widget.Tanggal Tanggal;
    private widget.Tanggal TanggalKKL;
    private widget.Tanggal TanggalRujuk;
    private widget.Tanggal TanggalSEP;
    private widget.TextBox TglLahir;
    private widget.ComboBox TujuanKunjungan;
    private widget.Button btnDPJPLayanan;
    private widget.Button btnDPJPLayanan1;
    private widget.Button btnDiagnosaAwal;
    private widget.Button btnDiagnosaAwal1;
    private widget.Button btnDiagnosaAwal2;
    private component.Button btnFingerPrint;
    private component.Button btnKeluar;
    private component.Button btnSimpan;
    private widget.InternalFrame internalFrame2;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel18;
    private widget.Label jLabel20;
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.Label jLabel24;
    private widget.Label jLabel25;
    private widget.Label jLabel26;
    private widget.Label jLabel27;
    private widget.Label jLabel36;
    private widget.Label jLabel37;
    private widget.Label jLabel38;
    private widget.Label jLabel40;
    private widget.Label jLabel41;
    private widget.Label jLabel42;
    private widget.Label jLabel43;
    private widget.Label jLabel44;
    private widget.Label jLabel45;
    private widget.Label jLabel55;
    private widget.Label jLabel56;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private component.Panel jPanel1;
    private javax.swing.JPanel jPanel3;
    private widget.TextBox kdpoli;
    private widget.TextBox nmpnj;
    private widget.ScrollPane scrollInput;
    // End of variables declaration//GEN-END:variables

    public void setPasien(String norm) {

    }

    public void isCek(String norm) {

    }

    private void UpdateUmur() {

    }

    private void isNumber() {

        if (Sequel.cariInteger("select count(booking_registrasi.no_rkm_medis) from booking_registrasi where booking_registrasi.no_rkm_medis='" + TNoRM.getText() + "' and "
                + "booking_registrasi.tanggal_periksa='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "' and booking_registrasi.kd_dokter='" + kodedokterreg + "' and booking_registrasi.kd_poli='" + kodepolireg + "'") > 0) {
                    NoReg.setText(Sequel.cariIsi("select booking_registrasi.no_reg from booking_registrasi where booking_registrasi.no_rkm_medis='" + TNoRM.getText() + "' and "
                    + "booking_registrasi.tanggal_periksa='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "' and booking_registrasi.kd_dokter='" + kodedokterreg + "' and booking_registrasi.kd_poli='" + kodepolireg + "'"));

        } else {
            if (BASENOREG.equals("booking")) {
                switch (URUTNOREG) {
                    case "poli":
                        if (Sequel.cariInteger("select ifnull(MAX(CONVERT(no_reg,signed)),0) from booking_registrasi where kd_poli='" + kodepolireg + "' and tanggal_periksa='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'")
                                >= Sequel.cariInteger("select ifnull(MAX(CONVERT(no_reg,signed)),0) from reg_periksa where kd_poli='" + kodepolireg + "' and tgl_registrasi='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'")) {
                            Valid.autoNomer3("select ifnull(MAX(CONVERT(no_reg,signed)),0) from booking_registrasi where kd_poli='" + kodepolireg + "' and tanggal_periksa='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'", "", 3, NoReg);
                        } else {
                            Valid.autoNomer3("select ifnull(MAX(CONVERT(no_reg,signed)),0) from reg_periksa where kd_poli='" + kodepolireg + "' and tgl_registrasi='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'", "", 3, NoReg);
                        }
                        break;
                    case "dokter":
                        if (Sequel.cariInteger("select ifnull(MAX(CONVERT(no_reg,signed)),0) from booking_registrasi where kd_dokter='" + kodedokterreg + "' and tanggal_periksa='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'")
                                >= Sequel.cariInteger("select ifnull(MAX(CONVERT(no_reg,signed)),0) from reg_periksa where kd_dokter='" + kodedokterreg + "' and tgl_registrasi='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'")) {
                            Valid.autoNomer3("select ifnull(MAX(CONVERT(no_reg,signed)),0) from booking_registrasi where kd_dokter='" + kodedokterreg + "' and tanggal_periksa='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'", "", 3, NoReg);
                        } else {
                            Valid.autoNomer3("select ifnull(MAX(CONVERT(no_reg,signed)),0) from reg_periksa where kd_dokter='" + kodedokterreg + "' and tgl_registrasi='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'", "", 3, NoReg);
                        }
                        break;
                    case "dokter + poli":
                        if (Sequel.cariInteger("select ifnull(MAX(CONVERT(no_reg,signed)),0) from booking_registrasi where kd_dokter='" + kodedokterreg + "' and kd_poli='" + kodepolireg + "' and tanggal_periksa='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'")
                                >= Sequel.cariInteger("select ifnull(MAX(CONVERT(no_reg,signed)),0) from reg_periksa where kd_dokter='" + kodedokterreg + "' and kd_poli='" + kodepolireg + "' and tgl_registrasi='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'")) {
                            Valid.autoNomer3("select ifnull(MAX(CONVERT(no_reg,signed)),0) from booking_registrasi where kd_dokter='" + kodedokterreg + "' and kd_poli='" + kodepolireg + "' and tanggal_periksa='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'", "", 3, NoReg);
                        } else {
                            Valid.autoNomer3("select ifnull(MAX(CONVERT(no_reg,signed)),0) from reg_periksa where kd_dokter='" + kodedokterreg + "' and kd_poli='" + kodepolireg + "' and tgl_registrasi='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'", "", 3, NoReg);
                        }
                        break;
                    default:
                        if (Sequel.cariInteger("select ifnull(MAX(CONVERT(no_reg,signed)),0) from booking_registrasi where kd_poli='" + kodepolireg + "' and tanggal_periksa='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'")
                                >= Sequel.cariInteger("select ifnull(MAX(CONVERT(no_reg,signed)),0) from reg_periksa where kd_poli='" + kodepolireg + "' and tgl_registrasi='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'")) {
                            Valid.autoNomer3("select ifnull(MAX(CONVERT(no_reg,signed)),0) from booking_registrasi where kd_poli='" + kodepolireg + "' and tanggal_periksa='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'", "", 3, NoReg);
                        } else {
                            Valid.autoNomer3("select ifnull(MAX(CONVERT(no_reg,signed)),0) from reg_periksa where kd_poli='" + kodepolireg + "' and tgl_registrasi='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'", "", 3, NoReg);
                        }
                        break;
                }
            } else {
                switch (URUTNOREG) {
                    case "poli":
                        Valid.autoNomer3("select ifnull(MAX(CONVERT(no_reg,signed)),0) from reg_periksa where kd_poli='" + kodepolireg + "' and tgl_registrasi='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'", "", 3, NoReg);
                        break;
                    case "dokter":
                        Valid.autoNomer3("select ifnull(MAX(CONVERT(no_reg,signed)),0) from reg_periksa where kd_dokter='" + kodedokterreg + "' and tgl_registrasi='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'", "", 3, NoReg);
                        break;
                    case "dokter + poli":
                        Valid.autoNomer3("select ifnull(MAX(CONVERT(no_reg,signed)),0) from reg_periksa where kd_dokter='" + kodedokterreg + "' and kd_poli='" + kode_poli + "' and tgl_registrasi='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'", "", 3, NoReg);
                        break;
                    default:
                        Valid.autoNomer3("select ifnull(MAX(CONVERT(no_reg,signed)),0) from reg_periksa where kd_dokter='" + kodedokterreg + "' and tgl_registrasi='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "'", "", 3, NoReg);
                        break;
                }
            }
        }

        Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(no_rawat,6),signed)),0) from reg_periksa where tgl_registrasi='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "' ", Valid.SetTgl(TanggalSEP.getSelectedItem().toString()).replaceAll("-", "/") + "/", 6, TNoRw);
    }

    private void tentukanHari() {
        try {
            java.sql.Date hariperiksa = java.sql.Date.valueOf(Valid.SetTgl(TanggalSEP.getSelectedItem().toString()));
            cal.setTime(hariperiksa);
            day = cal.get(Calendar.DAY_OF_WEEK);
            switch (day) {
                case 1:
                    hari = "AKHAD";
                    break;
                case 2:
                    hari = "SENIN";
                    break;
                case 3:
                    hari = "SELASA";
                    break;
                case 4:
                    hari = "RABU";
                    break;
                case 5:
                    hari = "KAMIS";
                    break;
                case 6:
                    hari = "JUMAT";
                    break;
                case 7:
                    hari = "SABTU";
                    break;
                default:
                    break;
            }
            System.out.println(hari);

        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }

    }

    private void isCekPasien() {
        try {
            ps3 = koneksi.prepareStatement("select nm_pasien,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) asal,"
                    + "namakeluarga,keluarga,pasien.kd_pj,penjab.png_jawab,if(tgl_daftar=?,'Baru','Lama') as daftar, "
                    + "TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) as tahun,pasien.no_peserta, "
                    + "(TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12)) as bulan, "
                    + "TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()) as hari,pasien.no_ktp,pasien.no_tlp "
                    + "from pasien inner join kelurahan on pasien.kd_kel=kelurahan.kd_kel "
                    + "inner join kecamatan on pasien.kd_kec=kecamatan.kd_kec "
                    + "inner join kabupaten on pasien.kd_kab=kabupaten.kd_kab "
                    + "inner join penjab on pasien.kd_pj=penjab.kd_pj "
                    + "where pasien.no_rkm_medis=?");
            try {
                ps3.setString(1, Valid.SetTgl(TanggalSEP.getSelectedItem() + ""));
                ps3.setString(2, TNoRM.getText());
                rs = ps3.executeQuery();
                while (rs.next()) {
                    TAlmt.setText(rs.getString("asal"));
                    TPngJwb.setText(rs.getString("namakeluarga"));
                    THbngn.setText(rs.getString("keluarga"));
                    NoTelpPasien.setText(rs.getString("no_tlp"));
                    umur = "0";
                    sttsumur = "Th";
                    statuspasien = rs.getString("daftar");
                    if (rs.getInt("tahun") > 0) {
                        umur = rs.getString("tahun");
                        sttsumur = "Th";
                    } else if (rs.getInt("tahun") == 0) {
                        if (rs.getInt("bulan") > 0) {
                            umur = rs.getString("bulan");
                            sttsumur = "Bl";
                        } else if (rs.getInt("bulan") == 0) {
                            umur = rs.getString("hari");
                            sttsumur = "Hr";
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex);
            } finally {
                if (rs != null) {
                    rs.close();
                }

                if (ps3 != null) {
                    ps3.close();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        status = "Baru";
        if (Sequel.cariInteger("select count(no_rkm_medis) from reg_periksa where no_rkm_medis=? and kd_poli=?", TNoRM.getText(), kodepolireg) > 0) {
            status = "Lama";
        }

    }

    private void MnCetakRegisterActionPerformed(String norawat) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        tentukanHari();
        Map<String, Object> param = new HashMap<>();
        param.put("namars", Sequel.cariIsi("select nama_instansi from setting"));
        param.put("alamatrs", Sequel.cariIsi("select alamat_instansi from setting"));
        param.put("kotars", Sequel.cariIsi("select kabupaten from setting"));
        param.put("propinsirs", Sequel.cariIsi("select propinsi from setting"));
        param.put("kontakrs", Sequel.cariIsi("select kontak from setting"));
        param.put("emailrs", Sequel.cariIsi("select email from setting"));
        param.put("logo", Sequel.cariGambar("select logo from setting"));
        Valid.MyReportqry("rptBuktiRegister.jasper", "report", "::[ Bukti Register ]::",
                "SELECT rp.no_reg, rp.no_rawat, rp.tgl_registrasi, d.nm_dokter, rp.no_rkm_medis, p.nm_pasien, po.nm_poli,\n" +
                "CONCAT(LEFT(j.jam_mulai,5),'-',LEFT(j.jam_selesai,5)) AS jampel, IFNULL(r.nobooking,'-') AS nobooking, IFNULL(r.nomorreferensi,'-') AS nomorreferensi\n" +
                "FROM reg_periksa rp\n" +
                "INNER JOIN dokter d ON d.kd_dokter = rp.kd_dokter\n" +
                "INNER JOIN pasien p ON p.no_rkm_medis = rp.no_rkm_medis\n" +
                "INNER JOIN poliklinik po ON po.kd_poli = rp.kd_poli\n" +
                "INNER JOIN jadwal j ON j.kd_dokter = rp.kd_dokter\n" +
                "LEFT JOIN referensi_mobilejkn_bpjs r ON r.no_rawat = rp.no_rawat\n" +
                "WHERE rp.no_rawat ='" + norawat + "' AND j.hari_kerja = '"+hari+"'", param);
        TulisLog("Cetak");
        TulisLog("SELECT rp.no_reg, rp.no_rawat, rp.tgl_registrasi, d.nm_dokter, rp.no_rkm_medis, p.nm_pasien, po.nm_poli, " +
                "CONCAT(LEFT(j.jam_mulai,5),'-',LEFT(j.jam_selesai,5)) AS jampel, IFNULL(r.nobooking,'-') AS nobooking, IFNULL(r.nomorreferensi,'-') AS nomorreferensi " +
                "FROM reg_periksa rp " +
                "INNER JOIN dokter d ON d.kd_dokter = rp.kd_dokter " +
                "INNER JOIN pasien p ON p.no_rkm_medis = rp.no_rkm_medis " +
                "INNER JOIN poliklinik po ON po.kd_poli = rp.kd_poli " +
                "INNER JOIN jadwal j ON j.kd_dokter = rp.kd_dokter " +
                "LEFT JOIN referensi_mobilejkn_bpjs r ON r.no_rawat = rp.no_rawat " +
                "WHERE rp.no_rawat ='" + norawat + "' AND j.hari_kerja = '"+hari+"' ");
//        System.out.println(norawat);
        this.setCursor(Cursor.getDefaultCursor());

    }

    private void insertSEP() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        TulisLog("Memulai fungsi insertSEP");
        try {
            tglkkl = "0000-00-00";
            if (LakaLantas.getSelectedIndex() > 0) {
                tglkkl = Valid.SetTgl(TanggalKKL.getSelectedItem() + "");
            }

            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            URL = link + "/SEP/2.0/insert";
            requestJson = "{"
                    + "\"request\":{"
                    + "\"t_sep\":{"
                    + "\"noKartu\":\"" + NoKartu.getText() + "\","
                    + "\"tglSep\":\"" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "\","
                    + "\"ppkPelayanan\":\"" + KdPPK.getText() + "\","
                    + "\"jnsPelayanan\":\"" + JenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\","
                    + "\"klsRawat\":{"
                    + "\"klsRawatHak\":\"" + Kelas.getSelectedItem().toString().substring(0, 1) + "\","
                    + "\"klsRawatNaik\":\"\","
                    + "\"pembiayaan\":\"\","
                    + "\"penanggungJawab\":\"\""
                    + "},"
                    + "\"noMR\":\"" + TNoRM.getText() + "\","
                    + "\"rujukan\": {"
                    + "\"asalRujukan\":\"" + AsalRujukan.getSelectedItem().toString().substring(0, 1) + "\","
                    + "\"tglRujukan\":\"" + Valid.SetTgl(TanggalRujuk.getSelectedItem() + "") + "\","
                    + "\"noRujukan\":\"" + NoRujukan.getText() + "\","
                    + "\"ppkRujukan\":\"" + KdPpkRujukan.getText() + "\""
                    + "},"
                    + "\"catatan\":\"" + Catatan.getText() + "\","
                    + "\"diagAwal\":\"" + KdPenyakit.getText() + "\","
                    + "\"poli\": {"
                    + "\"tujuan\": \"" + KdPoli.getText() + "\","
                    + "\"eksekutif\": \"0\""
                    + "},"
                    + "\"cob\": {"
                    + "\"cob\": \"0\""
                    + "},"
                    + "\"katarak\": {"
                    + "\"katarak\": \"" + Katarak.getSelectedItem().toString().substring(0, 1) + "\""
                    + "},"
                    + "\"jaminan\": {"
                    + "\"lakaLantas\":\"" + LakaLantas.getSelectedItem().toString().substring(0, 1) + "\","
                    + "\"penjamin\": {"
                    + "\"tglKejadian\": \"" + tglkkl.replaceAll("0000-00-00", "") + "\","
                    + "\"keterangan\": \"" + Keterangan.getText() + "\","
                    + "\"suplesi\": {"
                    + "\"suplesi\": \"" + Suplesi.getSelectedItem().toString().substring(0, 1) + "\","
                    + "\"noSepSuplesi\": \"" + NoSEPSuplesi.getText() + "\","
                    + "\"lokasiLaka\": {"
                    + "\"kdPropinsi\": \"" + KdPropinsi.getText() + "\","
                    + "\"kdKabupaten\": \"" + KdKabupaten.getText() + "\","
                    + "\"kdKecamatan\": \"" + KdKecamatan.getText() + "\""
                    + "}"
                    + "}"
                    + "}"
                    + "},"
                    + "\"tujuanKunj\": \"" + TujuanKunjungan.getSelectedItem().toString().substring(0, 1) + "\","
                    + "\"flagProcedure\": \"" + (FlagProsedur.getSelectedIndex() > 0 ? FlagProsedur.getSelectedItem().toString().substring(0, 1) : "") + "\","
                    + "\"kdPenunjang\": \"" + (Penunjang.getSelectedIndex() > 0 ? Penunjang.getSelectedIndex() + "" : "") + "\","
                    + "\"assesmentPel\": \"" + (AsesmenPoli.getSelectedIndex() > 0 ? AsesmenPoli.getSelectedItem().toString().substring(0, 1) : "") + "\","
                    + "\"skdp\": {"
                    + "\"noSurat\": \"" + NoSKDP.getText() + "\","
                    + "\"kodeDPJP\": \"" + KdDPJP.getText() + "\""
                    + "},"
                    + "\"dpjpLayan\": \"" + (KdDPJPLayanan.getText().equals("") ? "" : KdDPJPLayanan.getText()) + "\","
                    + "\"noTelp\": \"" + NoTelp.getText() + "\","
                    + "\"user\":\"" + NoKartu.getText() + "\""
                    + "}"
                    + "}"
                    + "}";
            requestEntity = new HttpEntity(requestJson, headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
            TulisLog("WS InsertSEP JSON: "+requestJson+" | URL "+URL);
            nameNode = root.path("metaData");
            System.out.println("code : " + nameNode.path("code").asText());
            JOptionPane.showMessageDialog(rootPane, "Respon BPJS : " + nameNode.path("message").asText());
            if (nameNode.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("sep").path("noSep");
//                if(checkinMJKN){
//                    
//                    //simpan ke bridging_sep
//                    if (Sequel.menyimpantf2("bridging_sep", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "SEP", 52, new String[]{
//                        response.asText(),
//                        TNoRw.getText(),
//                        Valid.SetTgl(TanggalSEP.getSelectedItem() + ""),
//                        Valid.SetTgl(TanggalRujuk.getSelectedItem() + ""),
//                        NoRujukan.getText(),
//                        KdPpkRujukan.getText(),
//                        NmPpkRujukan.getText(),
//                        KdPPK.getText(),
//                        NmPPK.getText(),
//                        JenisPelayanan.getSelectedItem().toString().substring(0, 1),
//                        Catatan.getText(),
//                        KdPenyakit.getText(),
//                        NmPenyakit.getText(),
//                        KdPoli.getText(),
//                        NmPoli.getText(),
//                        Kelas.getSelectedItem().toString().substring(0, 1),
//                        "",
//                        "",
//                        "",
//                        LakaLantas.getSelectedItem().toString().substring(0, 1),
//                        TNoRM.getText(),
//                        TNoRM.getText(),
//                        TPasien.getText(),
//                        TglLahir.getText(),
//                        JenisPeserta.getText(),
//                        JK.getText(),
//                        NoKartu.getText(),
//                        "0000-00-00 00:00:00",
//                        AsalRujukan.getSelectedItem().toString(),
//                        "0. Tidak",
//                        "0. Tidak",
//                        NoTelp.getText(),
//                        Katarak.getSelectedItem().toString(),
//                        tglkkl,
//                        Keterangan.getText(),
//                        Suplesi.getSelectedItem().toString(),
//                        NoSEPSuplesi.getText(),
//                        KdPropinsi.getText(),
//                        NmPropinsi.getText(),
//                        KdKabupaten.getText(),
//                        NmKabupaten.getText(),
//                        KdKecamatan.getText(),
//                        NmKecamatan.getText(),
//                        NoSKDP.getText(),
//                        KdDPJP.getText(),
//                        NmDPJP.getText(),
//                        TujuanKunjungan.getSelectedItem().toString().substring(0, 1),
//                        (FlagProsedur.getSelectedIndex() > 0 ? FlagProsedur.getSelectedItem().toString().substring(0, 1) : ""),
//                        (Penunjang.getSelectedIndex() > 0 ? Penunjang.getSelectedIndex() + "" : ""),
//                        (AsesmenPoli.getSelectedIndex() > 0 ? AsesmenPoli.getSelectedItem().toString().substring(0, 1) : ""),
//                        KdDPJPLayanan.getText(),
//                        NmDPJPLayanan.getText()
//                    }) == true) {
////                        CetakSEPOtomatis(response.asText());
//
//                    }
//                } else {
                    //cek apakah sudah ngeSIM (daftar dari halo soepraoen)
//                    if(Sequel.cariInteger("SELECT COUNT(*) FROM reg_periksa WHERE tgl_registrasi = '"+Valid.SetTgl(TanggalSEP.getSelectedItem() + "")+"' AND no_rkm_medis = '"+TNoRM.getText()+"'")>0){
//                        SimpanAntrianOnSite();
//                        Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(rujuk_masuk.no_rawat,4),signed)),0) from reg_periksa inner join rujuk_masuk on reg_periksa.no_rawat=rujuk_masuk.no_rawat where reg_periksa.tgl_registrasi='" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "' ", "BR/" + dateformat.format(TanggalSEP.getDate()) + "/", 4, NoRujukMasuk);
//                        Sequel.menyimpan("rujuk_masuk", "?,?,?,?,?,?,?,?,?,?", 10, new String[]{
//                            TNoRw.getText(), NmPpkRujukan.getText(), "-", NoRujukan.getText(), "0", NmPpkRujukan.getText(), KdPenyakit.getText(), "-",
//                            "-", NoRujukMasuk.getText()
//                        });
//
//                        if (Sequel.menyimpantf2("bridging_sep", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "SEP", 52, new String[]{
//                            response.asText(),
//                            TNoRw.getText(),
//                            Valid.SetTgl(TanggalSEP.getSelectedItem() + ""),
//                            Valid.SetTgl(TanggalRujuk.getSelectedItem() + ""),
//                            NoRujukan.getText(),
//                            KdPpkRujukan.getText(),
//                            NmPpkRujukan.getText(),
//                            KdPPK.getText(),
//                            NmPPK.getText(),
//                            JenisPelayanan.getSelectedItem().toString().substring(0, 1),
//                            Catatan.getText(),
//                            KdPenyakit.getText(),
//                            NmPenyakit.getText(),
//                            KdPoli.getText(),
//                            NmPoli.getText(),
//                            Kelas.getSelectedItem().toString().substring(0, 1),
//                            "",
//                            "",
//                            "",
//                            LakaLantas.getSelectedItem().toString().substring(0, 1),
//                            TNoRM.getText(),
//                            TNoRM.getText(),
//                            TPasien.getText(),
//                            TglLahir.getText(),
//                            JenisPeserta.getText(),
//                            JK.getText(),
//                            NoKartu.getText(),
//                            "0000-00-00 00:00:00",
//                            AsalRujukan.getSelectedItem().toString(),
//                            "0. Tidak",
//                            "0. Tidak",
//                            NoTelp.getText(),
//                            Katarak.getSelectedItem().toString(),
//                            tglkkl,
//                            Keterangan.getText(),
//                            Suplesi.getSelectedItem().toString(),
//                            NoSEPSuplesi.getText(),
//                            KdPropinsi.getText(),
//                            NmPropinsi.getText(),
//                            KdKabupaten.getText(),
//                            NmKabupaten.getText(),
//                            KdKecamatan.getText(),
//                            NmKecamatan.getText(),
//                            NoSKDP.getText(),
//                            KdDPJP.getText(),
//                            NmDPJP.getText(),
//                            TujuanKunjungan.getSelectedItem().toString().substring(0, 1),
//                            (FlagProsedur.getSelectedIndex() > 0 ? FlagProsedur.getSelectedItem().toString().substring(0, 1) : ""),
//                            (Penunjang.getSelectedIndex() > 0 ? Penunjang.getSelectedIndex() + "" : ""),
//                            (AsesmenPoli.getSelectedIndex() > 0 ? AsesmenPoli.getSelectedItem().toString().substring(0, 1) : ""),
//                            KdDPJPLayanan.getText(),
//                            NmDPJPLayanan.getText()
//                        }) == true) {
//    //                        CetakSEPOtomatis(response.asText());
//
//                        }
//
//                        if (!prb.equals("")) {
//                            if (Sequel.menyimpantf("bpjs_prb", "?,?", "PRB", 2, new String[]{response.asText(), prb}) == true) {
//                                prb = "";
//                            }
//                        }
//
//                        if (Sequel.cariInteger("select count(booking_registrasi.no_rkm_medis) from booking_registrasi where booking_registrasi.no_rkm_medis='" + TNoRM.getText() + "' and "
//                                + "booking_registrasi.tanggal_periksa='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "' and booking_registrasi.kd_dokter='" + kodedokterreg + "' and booking_registrasi.kd_poli='" + kodepolireg + "'") > 0) {
//                            Sequel.queryu2("update booking_registrasi set status='Terdaftar' where no_rkm_medis=? and tanggal_periksa=? and kd_dokter=? and kd_poli=? ", 4, new String[]{
//                                TNoRM.getText(), Valid.SetTgl(TanggalSEP.getSelectedItem().toString()), kodedokterreg, kodepolireg
//                            });
//                            Sequel.queryu2("update booking_registrasi set waktu_kunjungan=now() where no_rkm_medis=? and tanggal_periksa=? and kd_dokter=? and kd_poli=? ", 4, new String[]{
//                                TNoRM.getText(), Valid.SetTgl(TanggalSEP.getSelectedItem().toString()), kodedokterreg, kodepolireg
//                            });
//                        }
//                        MnCetakRegisterActionPerformed(TNoRw.getText());
//
//                        emptTeks();
//                        dispose();
//                    } else {
//                        isNumber();
                        TulisLog("WS InsertSEP berhasil");
                        if (Sequel.menyimpantf2("reg_periksa", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Rawat", 19,
                                new String[]{NoReg.getText(), TNoRw.getText(), Valid.SetTgl(TanggalSEP.getSelectedItem() + ""), Sequel.cariIsi("select current_time()"),
                                    kodedokterreg, TNoRM.getText(), kodepolireg, TPngJwb.getText(), TAlmt.getText(), THbngn.getText(), TBiaya.getText(), "Belum",
                                    statuspasien, "Ralan", Kdpnj.getText(), umur, sttsumur, "Belum Bayar", status}) == true) {
//                            SimpanAntrianOnSite();
                            TulisLog("Pendaftaran pasien berhasil");
                            Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(rujuk_masuk.no_rawat,4),signed)),0) from reg_periksa inner join rujuk_masuk on reg_periksa.no_rawat=rujuk_masuk.no_rawat where reg_periksa.tgl_registrasi='" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "' ", "BR/" + dateformat.format(TanggalSEP.getDate()) + "/", 4, NoRujukMasuk);
                            Sequel.menyimpan("rujuk_masuk", "?,?,?,?,?,?,?,?,?,?", 10, new String[]{
                                TNoRw.getText(), NmPpkRujukan.getText(), "-", NoRujukan.getText(), "0", NmPpkRujukan.getText(), KdPenyakit.getText(), "-",
                                "-", NoRujukMasuk.getText()
                            });
                            
                            if (Sequel.menyimpantf2("bridging_sep", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "SEP", 52, new String[]{
                                response.asText(),
                                TNoRw.getText(),
                                Valid.SetTgl(TanggalSEP.getSelectedItem() + ""),
                                Valid.SetTgl(TanggalRujuk.getSelectedItem() + ""),
                                NoRujukan.getText(),
                                KdPpkRujukan.getText(),
                                NmPpkRujukan.getText(),
                                KdPPK.getText(),
                                NmPPK.getText(),
                                JenisPelayanan.getSelectedItem().toString().substring(0, 1),
                                Catatan.getText(),
                                KdPenyakit.getText(),
                                NmPenyakit.getText(),
                                KdPoli.getText(),
                                NmPoli.getText(),
                                Kelas.getSelectedItem().toString().substring(0, 1),
                                "",
                                "",
                                "",
                                LakaLantas.getSelectedItem().toString().substring(0, 1),
                                TNoRM.getText(),
                                TNoRM.getText(),
                                TPasien.getText(),
                                TglLahir.getText(),
                                JenisPeserta.getText(),
                                JK.getText(),
                                NoKartu.getText(),
                                "0000-00-00 00:00:00",
                                AsalRujukan.getSelectedItem().toString(),
                                "0. Tidak",
                                "0. Tidak",
                                NoTelp.getText(),
                                Katarak.getSelectedItem().toString(),
                                tglkkl,
                                Keterangan.getText(),
                                Suplesi.getSelectedItem().toString(),
                                NoSEPSuplesi.getText(),
                                KdPropinsi.getText(),
                                NmPropinsi.getText(),
                                KdKabupaten.getText(),
                                NmKabupaten.getText(),
                                KdKecamatan.getText(),
                                NmKecamatan.getText(),
                                NoSKDP.getText(),
                                KdDPJP.getText(),
                                NmDPJP.getText(),
                                TujuanKunjungan.getSelectedItem().toString().substring(0, 1),
                                (FlagProsedur.getSelectedIndex() > 0 ? FlagProsedur.getSelectedItem().toString().substring(0, 1) : ""),
                                (Penunjang.getSelectedIndex() > 0 ? Penunjang.getSelectedIndex() + "" : ""),
                                (AsesmenPoli.getSelectedIndex() > 0 ? AsesmenPoli.getSelectedItem().toString().substring(0, 1) : ""),
                                KdDPJPLayanan.getText(),
                                NmDPJPLayanan.getText()
                            }) == true) {
        //                        CetakSEPOtomatis(response.asText());

                                if (!prb.equals("")) {
                                    if (Sequel.menyimpantf("bpjs_prb", "?,?", "PRB", 2, new String[]{response.asText(), prb}) == true) {
                                        prb = "";
                                    }
                                }

                                if (Sequel.cariInteger("select count(booking_registrasi.no_rkm_medis) from booking_registrasi where booking_registrasi.no_rkm_medis='" + TNoRM.getText() + "' and "
                                        + "booking_registrasi.tanggal_periksa='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "' and booking_registrasi.kd_dokter='" + kodedokterreg + "' and booking_registrasi.kd_poli='" + kodepolireg + "'") > 0) {
                                    Sequel.queryu2("update booking_registrasi set status='Terdaftar' where no_rkm_medis=? and tanggal_periksa=? and kd_dokter=? and kd_poli=? ", 4, new String[]{
                                        TNoRM.getText(), Valid.SetTgl(TanggalSEP.getSelectedItem().toString()), kodedokterreg, kodepolireg
                                    });
                                    Sequel.queryu2("update booking_registrasi set waktu_kunjungan=now() where no_rkm_medis=? and tanggal_periksa=? and kd_dokter=? and kd_poli=? ", 4, new String[]{
                                        TNoRM.getText(), Valid.SetTgl(TanggalSEP.getSelectedItem().toString()), kodedokterreg, kodepolireg
                                    });
                                }
                                MnCetakRegisterActionPerformed(TNoRw.getText());

                                emptTeks();
                                dispose();
                            } else {
                                JOptionPane.showMessageDialog(rootPane, "Proses simpan SEP gagal, dimohon untuk memfoto kotak hitam dan mengirimkan foto tsb ke IT. Terimakasih");
                                TulisLog("Simpan SEP gagal");
                            }
                        } else {
                            TulisLog("Pendaftaran pasien gagal, Mencoba lagi..");
                            //cek apakah no rawat sudah digunakan di referensi mobile jkn
                            String nobooking = Sequel.cariIsi("SELECT nobooking FROM referensi_mobilejkn_bpjs where no_rawat = '"+TNoRw.getText()+"'");
                            isNumber();
                            if (Sequel.menyimpantf2("reg_periksa", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Rawat", 19,
                                    new String[]{NoReg.getText(), TNoRw.getText(), Valid.SetTgl(TanggalSEP.getSelectedItem() + ""), Sequel.cariIsi("select current_time()"),
                                        kodedokterreg, TNoRM.getText(), kodepolireg, TPngJwb.getText(), TAlmt.getText(), THbngn.getText(), TBiaya.getText(), "Belum",
                                        statuspasien, "Ralan", Kdpnj.getText(), umur, sttsumur, "Belum Bayar", status}) == true) {
//                                SimpanAntrianOnSite();
                                //edit no rawat baru
                                if(nobooking != ""){
                                   TulisLog("merubah norawat di table referensi_mobilejkn_bpjs");
                                    Sequel.mengedit("referensi_mobilejkn_bpjs","nobooking='"+nobooking+"'","no_rawat='"+TNoRw.getText()+"'");
                                }
                                
                                TulisLog("Pendaftaran pasien berhasil");
                                Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(rujuk_masuk.no_rawat,4),signed)),0) from reg_periksa inner join rujuk_masuk on reg_periksa.no_rawat=rujuk_masuk.no_rawat where reg_periksa.tgl_registrasi='" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "' ", "BR/" + dateformat.format(TanggalSEP.getDate()) + "/", 4, NoRujukMasuk);
                                Sequel.menyimpan("rujuk_masuk", "?,?,?,?,?,?,?,?,?,?", 10, new String[]{
                                    TNoRw.getText(), NmPpkRujukan.getText(), "-", NoRujukan.getText(), "0", NmPpkRujukan.getText(), KdPenyakit.getText(), "-",
                                    "-", NoRujukMasuk.getText()
                                });

                                if (Sequel.menyimpantf2("bridging_sep", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "SEP", 52, new String[]{
                                    response.asText(),
                                    TNoRw.getText(),
                                    Valid.SetTgl(TanggalSEP.getSelectedItem() + ""),
                                    Valid.SetTgl(TanggalRujuk.getSelectedItem() + ""),
                                    NoRujukan.getText(),
                                    KdPpkRujukan.getText(),
                                    NmPpkRujukan.getText(),
                                    KdPPK.getText(),
                                    NmPPK.getText(),
                                    JenisPelayanan.getSelectedItem().toString().substring(0, 1),
                                    Catatan.getText(),
                                    KdPenyakit.getText(),
                                    NmPenyakit.getText(),
                                    KdPoli.getText(),
                                    NmPoli.getText(),
                                    Kelas.getSelectedItem().toString().substring(0, 1),
                                    "",
                                    "",
                                    "",
                                    LakaLantas.getSelectedItem().toString().substring(0, 1),
                                    TNoRM.getText(),
                                    TNoRM.getText(),
                                    TPasien.getText(),
                                    TglLahir.getText(),
                                    JenisPeserta.getText(),
                                    JK.getText(),
                                    NoKartu.getText(),
                                    "0000-00-00 00:00:00",
                                    AsalRujukan.getSelectedItem().toString(),
                                    "0. Tidak",
                                    "0. Tidak",
                                    NoTelp.getText(),
                                    Katarak.getSelectedItem().toString(),
                                    tglkkl,
                                    Keterangan.getText(),
                                    Suplesi.getSelectedItem().toString(),
                                    NoSEPSuplesi.getText(),
                                    KdPropinsi.getText(),
                                    NmPropinsi.getText(),
                                    KdKabupaten.getText(),
                                    NmKabupaten.getText(),
                                    KdKecamatan.getText(),
                                    NmKecamatan.getText(),
                                    NoSKDP.getText(),
                                    KdDPJP.getText(),
                                    NmDPJP.getText(),
                                    TujuanKunjungan.getSelectedItem().toString().substring(0, 1),
                                    (FlagProsedur.getSelectedIndex() > 0 ? FlagProsedur.getSelectedItem().toString().substring(0, 1) : ""),
                                    (Penunjang.getSelectedIndex() > 0 ? Penunjang.getSelectedIndex() + "" : ""),
                                    (AsesmenPoli.getSelectedIndex() > 0 ? AsesmenPoli.getSelectedItem().toString().substring(0, 1) : ""),
                                    KdDPJPLayanan.getText(),
                                    NmDPJPLayanan.getText()
                                }) == true) {
            //                        CetakSEPOtomatis(response.asText());

                                    if (!prb.equals("")) {
                                        if (Sequel.menyimpantf("bpjs_prb", "?,?", "PRB", 2, new String[]{response.asText(), prb}) == true) {
                                            prb = "";
                                        }
                                    }

                                    if (Sequel.cariInteger("select count(booking_registrasi.no_rkm_medis) from booking_registrasi where booking_registrasi.no_rkm_medis='" + TNoRM.getText() + "' and "
                                            + "booking_registrasi.tanggal_periksa='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "' and booking_registrasi.kd_dokter='" + kodedokterreg + "' and booking_registrasi.kd_poli='" + kodepolireg + "'") > 0) {
                                        Sequel.queryu2("update booking_registrasi set status='Terdaftar' where no_rkm_medis=? and tanggal_periksa=? and kd_dokter=? and kd_poli=? ", 4, new String[]{
                                            TNoRM.getText(), Valid.SetTgl(TanggalSEP.getSelectedItem().toString()), kodedokterreg, kodepolireg
                                        });
                                        Sequel.queryu2("update booking_registrasi set waktu_kunjungan=now() where no_rkm_medis=? and tanggal_periksa=? and kd_dokter=? and kd_poli=? ", 4, new String[]{
                                            TNoRM.getText(), Valid.SetTgl(TanggalSEP.getSelectedItem().toString()), kodedokterreg, kodepolireg
                                        });
                                    }
                                    MnCetakRegisterActionPerformed(TNoRw.getText());

                                    emptTeks();
                                    dispose();
                                } else {
                                    JOptionPane.showMessageDialog(rootPane, "Proses simpan SEP gagal, dimohon untuk memfoto kotak hitam dan mengirimkan foto tsb ke IT. Terimakasih");
                                    TulisLog("Simpan SEP gagal");
                                }
                            } else {
                                TulisLog("Pendaftaran pasien gagal, Mencoba lagi..");
                                //cek apakah no rawat sudah digunakan di referensi mobile jkn
                                nobooking = Sequel.cariIsi("SELECT nobooking FROM referensi_mobilejkn_bpjs where no_rawat = '"+TNoRw.getText()+"'");
                                isNumber();
                                if (Sequel.menyimpantf2("reg_periksa", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Rawat", 19,
                                        new String[]{NoReg.getText(), TNoRw.getText(), Valid.SetTgl(TanggalSEP.getSelectedItem() + ""), Sequel.cariIsi("select current_time()"),
                                            kodedokterreg, TNoRM.getText(), kodepolireg, TPngJwb.getText(), TAlmt.getText(), THbngn.getText(), TBiaya.getText(), "Belum",
                                            statuspasien, "Ralan", Kdpnj.getText(), umur, sttsumur, "Belum Bayar", status}) == true) {
//                                    SimpanAntrianOnSite();
                                    //edit no rawat baru
                                    if(nobooking != ""){
                                        TulisLog("merubah norawat di table referensi_mobilejkn_bpjs");
                                        Sequel.mengedit("referensi_mobilejkn_bpjs","nobooking='"+nobooking+"'","no_rawat='"+TNoRw.getText()+"'");
                                    }
                                    TulisLog("Pendaftaran pasien berhasil");
                                    Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(rujuk_masuk.no_rawat,4),signed)),0) from reg_periksa inner join rujuk_masuk on reg_periksa.no_rawat=rujuk_masuk.no_rawat where reg_periksa.tgl_registrasi='" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "' ", "BR/" + dateformat.format(TanggalSEP.getDate()) + "/", 4, NoRujukMasuk);
                                    Sequel.menyimpan("rujuk_masuk", "?,?,?,?,?,?,?,?,?,?", 10, new String[]{
                                        TNoRw.getText(), NmPpkRujukan.getText(), "-", NoRujukan.getText(), "0", NmPpkRujukan.getText(), KdPenyakit.getText(), "-",
                                        "-", NoRujukMasuk.getText()
                                    });

                                    if (Sequel.menyimpantf2("bridging_sep", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "SEP", 52, new String[]{
                                        response.asText(),
                                        TNoRw.getText(),
                                        Valid.SetTgl(TanggalSEP.getSelectedItem() + ""),
                                        Valid.SetTgl(TanggalRujuk.getSelectedItem() + ""),
                                        NoRujukan.getText(),
                                        KdPpkRujukan.getText(),
                                        NmPpkRujukan.getText(),
                                        KdPPK.getText(),
                                        NmPPK.getText(),
                                        JenisPelayanan.getSelectedItem().toString().substring(0, 1),
                                        Catatan.getText(),
                                        KdPenyakit.getText(),
                                        NmPenyakit.getText(),
                                        KdPoli.getText(),
                                        NmPoli.getText(),
                                        Kelas.getSelectedItem().toString().substring(0, 1),
                                        "",
                                        "",
                                        "",
                                        LakaLantas.getSelectedItem().toString().substring(0, 1),
                                        TNoRM.getText(),
                                        TNoRM.getText(),
                                        TPasien.getText(),
                                        TglLahir.getText(),
                                        JenisPeserta.getText(),
                                        JK.getText(),
                                        NoKartu.getText(),
                                        "0000-00-00 00:00:00",
                                        AsalRujukan.getSelectedItem().toString(),
                                        "0. Tidak",
                                        "0. Tidak",
                                        NoTelp.getText(),
                                        Katarak.getSelectedItem().toString(),
                                        tglkkl,
                                        Keterangan.getText(),
                                        Suplesi.getSelectedItem().toString(),
                                        NoSEPSuplesi.getText(),
                                        KdPropinsi.getText(),
                                        NmPropinsi.getText(),
                                        KdKabupaten.getText(),
                                        NmKabupaten.getText(),
                                        KdKecamatan.getText(),
                                        NmKecamatan.getText(),
                                        NoSKDP.getText(),
                                        KdDPJP.getText(),
                                        NmDPJP.getText(),
                                        TujuanKunjungan.getSelectedItem().toString().substring(0, 1),
                                        (FlagProsedur.getSelectedIndex() > 0 ? FlagProsedur.getSelectedItem().toString().substring(0, 1) : ""),
                                        (Penunjang.getSelectedIndex() > 0 ? Penunjang.getSelectedIndex() + "" : ""),
                                        (AsesmenPoli.getSelectedIndex() > 0 ? AsesmenPoli.getSelectedItem().toString().substring(0, 1) : ""),
                                        KdDPJPLayanan.getText(),
                                        NmDPJPLayanan.getText()
                                    }) == true) {
                //                        CetakSEPOtomatis(response.asText());
                                        if (!prb.equals("")) {
                                            if (Sequel.menyimpantf("bpjs_prb", "?,?", "PRB", 2, new String[]{response.asText(), prb}) == true) {
                                                prb = "";
                                            }
                                        }

                                        if (Sequel.cariInteger("select count(booking_registrasi.no_rkm_medis) from booking_registrasi where booking_registrasi.no_rkm_medis='" + TNoRM.getText() + "' and "
                                                + "booking_registrasi.tanggal_periksa='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "' and booking_registrasi.kd_dokter='" + kodedokterreg + "' and booking_registrasi.kd_poli='" + kodepolireg + "'") > 0) {
                                            Sequel.queryu2("update booking_registrasi set status='Terdaftar' where no_rkm_medis=? and tanggal_periksa=? and kd_dokter=? and kd_poli=? ", 4, new String[]{
                                                TNoRM.getText(), Valid.SetTgl(TanggalSEP.getSelectedItem().toString()), kodedokterreg, kodepolireg
                                            });
                                            Sequel.queryu2("update booking_registrasi set waktu_kunjungan=now() where no_rkm_medis=? and tanggal_periksa=? and kd_dokter=? and kd_poli=? ", 4, new String[]{
                                                TNoRM.getText(), Valid.SetTgl(TanggalSEP.getSelectedItem().toString()), kodedokterreg, kodepolireg
                                            });
                                        }
                                        MnCetakRegisterActionPerformed(TNoRw.getText());

                                        emptTeks();
                                        dispose();
                                    } else {
                                        JOptionPane.showMessageDialog(rootPane, "Proses simpan SEP gagal, dimohon untuk memfoto kotak hitam dan mengirimkan foto tsb ke IT. Terimakasih");
                                        TulisLog("Simpan SEP gagal");
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(rootPane, "Pembuatan SEP berhasil, tetapi pendaftaran pasien gagal. Hubungi loket untuk SIM manual...!");
                                    TulisLog("Pembuatan SEP berhasil, tetapi pendaftaran pasien gagal. Hubungi loket untuk SIM manual...!");
                                }
                            }
                        }
//                    }
//                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Simpan SEP gagal, harap hubungi loket pendaftaran...!\n"+nameNode.path("message").asText());
//                TulisLog("respon WS BPJS Insert SEP "+ TNoRM.getText()+ " : " + nameNode.path("code").asText() + " " + nameNode.path("message").asText() + "\n");
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Bridging : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }
    
//    private void insertSEP2() {
//        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//        try {
//            tglkkl = "0000-00-00";
//            if (LakaLantas.getSelectedIndex() > 0) {
//                tglkkl = Valid.SetTgl(TanggalKKL.getSelectedItem() + "");
//            }
//
//            headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
//            utc = String.valueOf(api.GetUTCdatetimeAsString());
//            headers.add("X-Timestamp", utc);
//            headers.add("X-Signature", api.getHmac(utc));
//            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
//            URL = link + "/SEP/2.0/insert";
//            requestJson = "{"
//                    + "\"request\":{"
//                    + "\"t_sep\":{"
//                    + "\"noKartu\":\"" + NoKartu.getText() + "\","
//                    + "\"tglSep\":\"" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "\","
//                    + "\"ppkPelayanan\":\"" + KdPPK.getText() + "\","
//                    + "\"jnsPelayanan\":\"" + JenisPelayanan.getSelectedItem().toString().substring(0, 1) + "\","
//                    + "\"klsRawat\":{"
//                    + "\"klsRawatHak\":\"" + Kelas.getSelectedItem().toString().substring(0, 1) + "\","
//                    + "\"klsRawatNaik\":\"\","
//                    + "\"pembiayaan\":\"\","
//                    + "\"penanggungJawab\":\"\""
//                    + "},"
//                    + "\"noMR\":\"" + TNoRM.getText() + "\","
//                    + "\"rujukan\": {"
//                    + "\"asalRujukan\":\"" + AsalRujukan.getSelectedItem().toString().substring(0, 1) + "\","
//                    + "\"tglRujukan\":\"" + Valid.SetTgl(TanggalRujuk.getSelectedItem() + "") + "\","
//                    + "\"noRujukan\":\"" + NoRujukan.getText() + "\","
//                    + "\"ppkRujukan\":\"" + KdPpkRujukan.getText() + "\""
//                    + "},"
//                    + "\"catatan\":\"" + Catatan.getText() + "\","
//                    + "\"diagAwal\":\"" + KdPenyakit.getText() + "\","
//                    + "\"poli\": {"
//                    + "\"tujuan\": \"" + KdPoli.getText() + "\","
//                    + "\"eksekutif\": \"0\""
//                    + "},"
//                    + "\"cob\": {"
//                    + "\"cob\": \"0\""
//                    + "},"
//                    + "\"katarak\": {"
//                    + "\"katarak\": \"" + Katarak.getSelectedItem().toString().substring(0, 1) + "\""
//                    + "},"
//                    + "\"jaminan\": {"
//                    + "\"lakaLantas\":\"" + LakaLantas.getSelectedItem().toString().substring(0, 1) + "\","
//                    + "\"penjamin\": {"
//                    + "\"tglKejadian\": \"" + tglkkl.replaceAll("0000-00-00", "") + "\","
//                    + "\"keterangan\": \"" + Keterangan.getText() + "\","
//                    + "\"suplesi\": {"
//                    + "\"suplesi\": \"" + Suplesi.getSelectedItem().toString().substring(0, 1) + "\","
//                    + "\"noSepSuplesi\": \"" + NoSEPSuplesi.getText() + "\","
//                    + "\"lokasiLaka\": {"
//                    + "\"kdPropinsi\": \"" + KdPropinsi.getText() + "\","
//                    + "\"kdKabupaten\": \"" + KdKabupaten.getText() + "\","
//                    + "\"kdKecamatan\": \"" + KdKecamatan.getText() + "\""
//                    + "}"
//                    + "}"
//                    + "}"
//                    + "},"
//                    + "\"tujuanKunj\": \"" + TujuanKunjungan.getSelectedItem().toString().substring(0, 1) + "\","
//                    + "\"flagProcedure\": \"" + (FlagProsedur.getSelectedIndex() > 0 ? FlagProsedur.getSelectedItem().toString().substring(0, 1) : "") + "\","
//                    + "\"kdPenunjang\": \"" + (Penunjang.getSelectedIndex() > 0 ? Penunjang.getSelectedIndex() + "" : "") + "\","
//                    + "\"assesmentPel\": \"" + (AsesmenPoli.getSelectedIndex() > 0 ? AsesmenPoli.getSelectedItem().toString().substring(0, 1) : "") + "\","
//                    + "\"skdp\": {"
//                    + "\"noSurat\": \"" + NoSKDP.getText() + "\","
//                    + "\"kodeDPJP\": \"" + KdDPJP.getText() + "\""
//                    + "},"
//                    + "\"dpjpLayan\": \"" + (KdDPJPLayanan.getText().equals("") ? "" : KdDPJPLayanan.getText()) + "\","
//                    + "\"noTelp\": \"" + NoTelp.getText() + "\","
//                    + "\"user\":\"" + NoKartu.getText() + "\""
//                    + "}"
//                    + "}"
//                    + "}";
//            requestEntity = new HttpEntity(requestJson, headers);
//            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
//            nameNode = root.path("metaData");
//            System.out.println("code : " + nameNode.path("code").asText());
//            JOptionPane.showMessageDialog(rootPane, "Respon BPJS : " + nameNode.path("message").asText());
//            if (nameNode.path("code").asText().equals("200")) {
//                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("sep").path("noSep");
//                
//                Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(rujuk_masuk.no_rawat,4),signed)),0) from reg_periksa inner join rujuk_masuk on reg_periksa.no_rawat=rujuk_masuk.no_rawat where reg_periksa.tgl_registrasi='" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "' ", "BR/" + dateformat.format(TanggalSEP.getDate()) + "/", 4, NoRujukMasuk);
//                Sequel.menyimpan("rujuk_masuk", "?,?,?,?,?,?,?,?,?,?", 10, new String[]{
//                    TNoRw.getText(), NmPpkRujukan.getText(), "-", NoRujukan.getText(), "0", NmPpkRujukan.getText(), KdPenyakit.getText(), "-",
//                    "-", NoRujukMasuk.getText()
//                });
//
//                if (Sequel.menyimpantf2("bridging_sep", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "SEP", 52, new String[]{
//                    response.asText(),
//                    TNoRw.getText(),
//                    Valid.SetTgl(TanggalSEP.getSelectedItem() + ""),
//                    Valid.SetTgl(TanggalRujuk.getSelectedItem() + ""),
//                    NoRujukan.getText(),
//                    KdPpkRujukan.getText(),
//                    NmPpkRujukan.getText(),
//                    KdPPK.getText(),
//                    NmPPK.getText(),
//                    JenisPelayanan.getSelectedItem().toString().substring(0, 1),
//                    Catatan.getText(),
//                    KdPenyakit.getText(),
//                    NmPenyakit.getText(),
//                    KdPoli.getText(),
//                    NmPoli.getText(),
//                    Kelas.getSelectedItem().toString().substring(0, 1),
//                    "",
//                    "",
//                    "",
//                    LakaLantas.getSelectedItem().toString().substring(0, 1),
//                    TNoRM.getText(),
//                    TNoRM.getText(),
//                    TPasien.getText(),
//                    TglLahir.getText(),
//                    JenisPeserta.getText(),
//                    JK.getText(),
//                    NoKartu.getText(),
//                    "0000-00-00 00:00:00",
//                    AsalRujukan.getSelectedItem().toString(),
//                    "0. Tidak",
//                    "0. Tidak",
//                    NoTelp.getText(),
//                    Katarak.getSelectedItem().toString(),
//                    tglkkl,
//                    Keterangan.getText(),
//                    Suplesi.getSelectedItem().toString(),
//                    NoSEPSuplesi.getText(),
//                    KdPropinsi.getText(),
//                    NmPropinsi.getText(),
//                    KdKabupaten.getText(),
//                    NmKabupaten.getText(),
//                    KdKecamatan.getText(),
//                    NmKecamatan.getText(),
//                    NoSKDP.getText(),
//                    KdDPJP.getText(),
//                    NmDPJP.getText(),
//                    TujuanKunjungan.getSelectedItem().toString().substring(0, 1),
//                    (FlagProsedur.getSelectedIndex() > 0 ? FlagProsedur.getSelectedItem().toString().substring(0, 1) : ""),
//                    (Penunjang.getSelectedIndex() > 0 ? Penunjang.getSelectedIndex() + "" : ""),
//                    (AsesmenPoli.getSelectedIndex() > 0 ? AsesmenPoli.getSelectedItem().toString().substring(0, 1) : ""),
//                    KdDPJPLayanan.getText(),
//                    NmDPJPLayanan.getText()
//                }) == true) {
//    //                        CetakSEPOtomatis(response.asText());
//
//                }
//
//                if (!prb.equals("")) {
//                    if (Sequel.menyimpantf("bpjs_prb", "?,?", "PRB", 2, new String[]{response.asText(), prb}) == true) {
//                        prb = "";
//                    }
//                }
//
//                if (Sequel.cariInteger("select count(booking_registrasi.no_rkm_medis) from booking_registrasi where booking_registrasi.no_rkm_medis='" + TNoRM.getText() + "' and "
//                        + "booking_registrasi.tanggal_periksa='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "' and booking_registrasi.kd_dokter='" + kodedokterreg + "' and booking_registrasi.kd_poli='" + kodepolireg + "'") > 0) {
//                    Sequel.queryu2("update booking_registrasi set status='Terdaftar' where no_rkm_medis=? and tanggal_periksa=? and kd_dokter=? and kd_poli=? ", 4, new String[]{
//                        TNoRM.getText(), Valid.SetTgl(TanggalSEP.getSelectedItem().toString()), kodedokterreg, kodepolireg
//                    });
//                    Sequel.queryu2("update booking_registrasi set waktu_kunjungan=now() where no_rkm_medis=? and tanggal_periksa=? and kd_dokter=? and kd_poli=? ", 4, new String[]{
//                        TNoRM.getText(), Valid.SetTgl(TanggalSEP.getSelectedItem().toString()), kodedokterreg, kodepolireg
//                    });
//                }
//                MnCetakRegisterActionPerformed(TNoRw.getText());
//
//                emptTeks();
//                dispose();
//            } else {
//                JOptionPane.showMessageDialog(rootPane, "Simpan SEP gagal, harap hubungi loket pendaftaran...!\n"+nameNode.path("message").asText());
////                TulisLog("respon WS BPJS Insert SEP "+ TNoRM.getText()+ " : " + nameNode.path("code").asText() + " " + nameNode.path("message").asText() + "\n");
//            }
//        } catch (Exception ex) {
//            System.out.println("Notifikasi Bridging : " + ex);
//            if (ex.toString().contains("UnknownHostException")) {
//                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
//            }
//        }
//        this.setCursor(Cursor.getDefaultCursor());
//    }

    private void cekFinger(String noka) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusfinger = false;

        if (!NoKartu.getText().equals("")) {
            try {
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                utc = String.valueOf(api.GetUTCdatetimeAsString());
                headers.add("X-Timestamp", utc);
                headers.add("X-Signature", api.getHmac(utc));
                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                URL = link + "/SEP/FingerPrint/Peserta/" + noka + "/TglPelayanan/" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "");
                requestEntity = new HttpEntity(headers);
                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
                nameNode = root.path("metaData");
                System.out.println("kodecekstatus : " + nameNode.path("code").asText());
                //System.out.println("message : "+nameNode.path("message").asText());
                if (nameNode.path("code").asText().equals("200")) {
                    response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc));
                    if (response.path("kode").asText().equals("1")) {
                        if (response.path("status").asText().contains(Sequel.cariIsi("select current_date()"))) {
                            statusfinger = true;
                        } else {
                            statusfinger = false;
                            JOptionPane.showMessageDialog(rootPane, response.path("status").asText());
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(rootPane, response.path("status").asText());
                }
            } catch (Exception ex) {
                System.out.println("Notifikasi Bridging : " + ex);
                if (ex.toString().contains("UnknownHostException")) {
                    JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Maaf, silahkan pilih data peserta!");
        }

        this.setCursor(Cursor.getDefaultCursor());
    }

    public void tampil(String nomorrujukan) {
        
        checkinMJKN = false;
        jenisKunjungan="";
        nomorReferensi="";
        try {
            URL = link + "/Rujukan/Peserta/" + nomorrujukan;
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            requestEntity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");
            System.out.println("URL : " + URL);
            peserta = "";
            if (nameNode.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                KdPenyakit.setText(response.path("diagnosa").path("kode").asText());
                NmPenyakit.setText(response.path("diagnosa").path("nama").asText());
                NoRujukan.setText(response.path("noKunjungan").asText());
                Kelas.setSelectedItem(response.path("peserta").path("hakKelas").path("kode").asText() + ". " + response.path("peserta").path("hakKelas").path("keterangan").asText().replaceAll("KELAS", "Kelas"));
                prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
                NoTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                TPasien.setText(response.path("peserta").path("nama").asText());
                NoKartu.setText(response.path("peserta").path("noKartu").asText());
                TNoRM.setText(Sequel.cariIsi("select pasien.no_rkm_medis from pasien where pasien.no_peserta='" + NoKartu.getText() + "'"));
                NIK.setText(Sequel.cariIsi("select pasien.no_ktp from pasien where pasien.no_rkm_medis='" + TNoRM.getText() + "'"));
                JK.setText(response.path("peserta").path("sex").asText());
                Status.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                TglLahir.setText(response.path("peserta").path("tglLahir").asText());
                KdPoli.setText(response.path("poliRujukan").path("kode").asText());
                NmPoli.setText(response.path("poliRujukan").path("nama").asText());
                JenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                kdpoli.setText(Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText()));
                kodepolireg = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText());
                kodedokterreg = Sequel.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", KdDPJP.getText());
//                if (!kodepolireg.equals("")) {
//                    isPoli();
//                } else {
//                    isPoli();
//                }
                KdPpkRujukan.setText(response.path("provPerujuk").path("kode").asText());
                NmPpkRujukan.setText(response.path("provPerujuk").path("nama").asText());
                Valid.SetTgl(TanggalRujuk, response.path("tglKunjungan").asText());
                isNumber();
                Kdpnj.setText("BPJ");
                nmpnj.setText("BPJS");
                
                TujuanKunjungan.setSelectedIndex(0);
                FlagProsedur.setSelectedIndex(0);
                Penunjang.setSelectedIndex(0);
                AsesmenPoli.setSelectedIndex(0);
                AsalRujukan.setSelectedIndex(0);
                
                //get tanggal habis rujukan
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Calendar c = Calendar.getInstance();
                c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(response.path("tglKunjungan").asText()));
                c.add(Calendar.DATE, 89); // Adding 5 days
                String tglHabis = sdf.format(c.getTime());
                jenisKunjungan = "1 (Rujukan FKTP)";
                nomorReferensi = response.path("noKunjungan").asText();
                Catatan.setText("SKP "+tglHabis+" | "+inisial_petugas);
                
                ps = koneksi.prepareStatement(
                        "select maping_dokter_dpjpvclaim.kd_dokter,maping_dokter_dpjpvclaim.kd_dokter_bpjs,maping_dokter_dpjpvclaim.nm_dokter_bpjs from maping_dokter_dpjpvclaim inner join jadwal "
                        + "on maping_dokter_dpjpvclaim.kd_dokter=jadwal.kd_dokter where jadwal.kd_poli=? and jadwal.hari_kerja=?");
                try {
                    if (day == 1) {
                        hari = "AKHAD";
                    } else if (day == 2) {
                        hari = "SENIN";
                    } else if (day == 3) {
                        hari = "SELASA";
                    } else if (day == 4) {
                        hari = "RABU";
                    } else if (day == 5) {
                        hari = "KAMIS";
                    } else if (day == 6) {
                        hari = "JUMAT";
                    } else if (day == 7) {
                        hari = "SABTU";
                    }

                    ps.setString(1, kdpoli.getText());
                    ps.setString(2, hari);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        KdDPJP.setText(rs.getString("kd_dokter_bpjs"));
                        NmDPJP.setText(rs.getString("nm_dokter_bpjs"));
                    }
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }
            } else {
                emptTeks();
//                dispose();
                JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Peserta : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }
    }
    
    public void tampilNoRujukan(String nomorrujukan) {
        checkinMJKN = false;
        jenisKunjungan="";
        nomorReferensi="";
        try {
            URL = link + "/Rujukan/" + nomorrujukan;
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            requestEntity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");
            System.out.println("URL : " + URL);
            peserta = "";
            if (nameNode.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                KdPenyakit.setText(response.path("diagnosa").path("kode").asText());
                NmPenyakit.setText(response.path("diagnosa").path("nama").asText());
                NoRujukan.setText(response.path("noKunjungan").asText());
                Kelas.setSelectedItem(response.path("peserta").path("hakKelas").path("kode").asText() + ". " + response.path("peserta").path("hakKelas").path("keterangan").asText().replaceAll("KELAS", "Kelas"));
                prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
                NoTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                TPasien.setText(response.path("peserta").path("nama").asText());
                NoKartu.setText(response.path("peserta").path("noKartu").asText());
                TNoRM.setText(Sequel.cariIsi("select pasien.no_rkm_medis from pasien where pasien.no_peserta='" + NoKartu.getText() + "'"));
                NIK.setText(Sequel.cariIsi("select pasien.no_ktp from pasien where pasien.no_rkm_medis='" + TNoRM.getText() + "'"));
                JK.setText(response.path("peserta").path("sex").asText());
                Status.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                TglLahir.setText(response.path("peserta").path("tglLahir").asText());
                KdPoli.setText(response.path("poliRujukan").path("kode").asText());
                NmPoli.setText(response.path("poliRujukan").path("nama").asText());
                JenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                kdpoli.setText(Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText()));
                kodepolireg = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText());
                kodedokterreg = Sequel.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", KdDPJP.getText());
//                if (!kodepolireg.equals("")) {
//                    isPoli();
//                } else {
//                    isPoli();
//                }
                KdPpkRujukan.setText(response.path("provPerujuk").path("kode").asText());
                NmPpkRujukan.setText(response.path("provPerujuk").path("nama").asText());
                Valid.SetTgl(TanggalRujuk, response.path("tglKunjungan").asText());
                isNumber();
                Kdpnj.setText("BPJ");
                nmpnj.setText("BPJS");
                
                TujuanKunjungan.setSelectedIndex(0);
                FlagProsedur.setSelectedIndex(0);
                Penunjang.setSelectedIndex(0);
                AsesmenPoli.setSelectedIndex(0);
                AsalRujukan.setSelectedIndex(0);
                
                //get tanggal habis rujukan
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Calendar c = Calendar.getInstance();
                c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(response.path("tglKunjungan").asText()));
                c.add(Calendar.DATE, 89); // Adding 5 days
                String tglHabis = sdf.format(c.getTime());
                jenisKunjungan = "1 (Rujukan FKTP)";
                nomorReferensi = response.path("noKunjungan").asText();
                Catatan.setText("SKP "+tglHabis+" | "+inisial_petugas);
                
                ps = koneksi.prepareStatement(
                        "select maping_dokter_dpjpvclaim.kd_dokter,maping_dokter_dpjpvclaim.kd_dokter_bpjs,maping_dokter_dpjpvclaim.nm_dokter_bpjs from maping_dokter_dpjpvclaim inner join jadwal "
                        + "on maping_dokter_dpjpvclaim.kd_dokter=jadwal.kd_dokter where jadwal.kd_poli=? and jadwal.hari_kerja=?");
                try {
                    if (day == 1) {
                        hari = "AKHAD";
                    } else if (day == 2) {
                        hari = "SENIN";
                    } else if (day == 3) {
                        hari = "SELASA";
                    } else if (day == 4) {
                        hari = "RABU";
                    } else if (day == 5) {
                        hari = "KAMIS";
                    } else if (day == 6) {
                        hari = "JUMAT";
                    } else if (day == 7) {
                        hari = "SABTU";
                    }

                    ps.setString(1, kdpoli.getText());
                    ps.setString(2, hari);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        KdDPJP.setText(rs.getString("kd_dokter_bpjs"));
                        NmDPJP.setText(rs.getString("nm_dokter_bpjs"));
                    }
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                }
            } else {
                emptTeks();
//                dispose();
                JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Peserta : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }
    }

    public void tampilPecahSEP(String nomorrujukan) {
        try {
            URL = link + "/Rujukan/Peserta/" + nomorrujukan;
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            requestEntity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");
            System.out.println("URL : " + URL);
            peserta = "";
            if (nameNode.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                KdPenyakit.setText(response.path("diagnosa").path("kode").asText());
                NmPenyakit.setText(response.path("diagnosa").path("nama").asText());
                NoRujukan.setText(response.path("noKunjungan").asText());
                Kelas.setSelectedItem(response.path("peserta").path("hakKelas").path("kode").asText() + ". " + response.path("peserta").path("hakKelas").path("keterangan").asText().replaceAll("KELAS", "Kelas"));
                prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
                NoTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                TPasien.setText(response.path("peserta").path("nama").asText());
                NoKartu.setText(response.path("peserta").path("noKartu").asText());
                TNoRM.setText(Sequel.cariIsi("select pasien.no_rkm_medis from pasien where pasien.no_peserta='" + NoKartu.getText() + "'"));
                NIK.setText(Sequel.cariIsi("select pasien.no_ktp from pasien where pasien.no_rkm_medis='" + TNoRM.getText() + "'"));
                JK.setText(response.path("peserta").path("sex").asText());
                Status.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                TglLahir.setText(response.path("peserta").path("tglLahir").asText());
                KdPoli.setText(response.path("poliRujukan").path("kode").asText());
                NmPoli.setText(response.path("poliRujukan").path("nama").asText());
                AsesmenPoli.setSelectedIndex(1);
                JenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                kdpoli.setText(Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText()));
                kodepolireg = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText());
                kodedokterreg = Sequel.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", KdDPJP.getText());
//                if (!kodepolireg.equals("")) {
//                    isPoli();
//                } else {
//                    isPoli();
//                }
                KdPpkRujukan.setText(response.path("provPerujuk").path("kode").asText());
                NmPpkRujukan.setText(response.path("provPerujuk").path("nama").asText());
                Valid.SetTgl(TanggalRujuk, response.path("tglKunjungan").asText());
                isNumber();
                Kdpnj.setText("BPJ");
                nmpnj.setText("BPJS");
                Catatan.setText("Anjungan Mandiri RS Indriati Boyolali");

            } else {
                emptTeks();
//                dispose();
                JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi Peserta : " + ex);
            if (ex.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
            }
        }
    }

    public void tampilKontrol(String noSKDP, String jnsSurkon) {
        checkinMJKN = false;
        noSKDP = noSKDP.toUpperCase();
        String noSEP="";
        String nokapesertakontrol = "";
        String tglkontrol = "";
        jenisKunjungan="";
        nomorReferensi="";
        kd_poli_rujukan = "";
//        System.out.println("inisial "+inisial_petugas);
        if(jnsSurkon == "bpjs"){
            noSEP = Sequel.cariIsi("select bridging_surat_kontrol_bpjs.no_sep from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'");
            nokapesertakontrol = Sequel.cariIsi("SELECT\n"
                    + "	bridging_sep.no_kartu\n"
                    + "FROM\n"
                    + "	bridging_sep where bridging_sep.no_sep='" + noSEP + "' ");
            tglkontrol = Sequel.cariIsi("select bridging_surat_kontrol_bpjs.tgl_rencana from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'");

            if (!tglkontrol.equals(Sequel.cariIsi("select current_date()"))) {
                String kdDokterKontrol = Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'");
                String KdPoliKontrol = Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_poli_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'");
                UpdateSuratKontrol(noSKDP, noSEP, kdDokterKontrol, KdPoliKontrol, Sequel.cariIsi("select current_date()"), "anjungan");
            }
            KdPoli.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_poli_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
            NmPoli.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.nm_poli_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
            KdDPJP.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
            NmDPJP.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.nm_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
            KdDPJPLayanan.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
            NmDPJPLayanan.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.nm_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
            NoSKDP.setText(noSKDP);
            jenisKunjungan="3 (Kontrol)";
            nomorReferensi =noSKDP;
            
        } else if (jnsSurkon == "internal"){
            noSEP = Sequel.cariIsi("select surat_kontrol_internal.no_sep from surat_kontrol_internal where surat_kontrol_internal.no_surat='" + noSKDP + "'");
            nokapesertakontrol = Sequel.cariIsi("SELECT\n"
                    + "	bridging_sep.no_kartu\n"
                    + "FROM\n"
                    + "	bridging_sep where bridging_sep.no_sep='" + noSEP + "' ");
            tglkontrol = Sequel.cariIsi("select surat_kontrol_internal.tgl_rencana from surat_kontrol_internal where surat_kontrol_internal.no_surat='" + noSKDP + "'");

            if (!tglkontrol.equals(Sequel.cariIsi("select current_date()"))) {
                String kdDokterKontrol = Sequel.cariIsi("select surat_kontrol_internal.kd_dokter_bpjs from surat_kontrol_internal where surat_kontrol_internal.no_surat='" + noSKDP + "'");
                String KdPoliKontrol = Sequel.cariIsi("select surat_kontrol_internal.kd_poli_bpjs from surat_kontrol_internal where surat_kontrol_internal.no_surat='" + noSKDP + "'");
                UpdateSuratKontrolInternal(noSKDP, noSEP, kdDokterKontrol, KdPoliKontrol, Sequel.cariIsi("select current_date()"), "anjungan");
            }
            KdPoli.setText(Sequel.cariIsi("select surat_kontrol_internal.kd_poli_bpjs from surat_kontrol_internal where surat_kontrol_internal.no_surat='" + noSKDP + "'"));
            NmPoli.setText(Sequel.cariIsi("select surat_kontrol_internal.nm_poli_bpjs from surat_kontrol_internal where surat_kontrol_internal.no_surat='" + noSKDP + "'"));
            KdDPJP.setText(Sequel.cariIsi("select surat_kontrol_internal.kd_dokter_bpjs from surat_kontrol_internal where surat_kontrol_internal.no_surat='" + noSKDP + "'"));
            NmDPJP.setText(Sequel.cariIsi("select surat_kontrol_internal.nm_dokter_bpjs from surat_kontrol_internal where surat_kontrol_internal.no_surat='" + noSKDP + "'"));
            KdDPJPLayanan.setText(Sequel.cariIsi("select surat_kontrol_internal.kd_dokter_bpjs from surat_kontrol_internal where surat_kontrol_internal.no_surat='" + noSKDP + "'"));
            NmDPJPLayanan.setText(Sequel.cariIsi("select surat_kontrol_internal.nm_dokter_bpjs from surat_kontrol_internal where surat_kontrol_internal.no_surat='" + noSKDP + "'"));
            NoSKDP.setText("");
            jenisKunjungan="2 (Rujukan Internal)";
            nomorReferensi =noSKDP;
        }
        

        //POST MRS
        if (Sequel.cariIsi("SELECT\n"
                + "	bridging_sep.jnspelayanan\n"
                + "FROM\n"
                + "	bridging_sep where bridging_sep.no_sep='" + noSEP + "' ").equals("1")) {
//            kondisi post ranap
            try {
                URL = link + "/Peserta/nokartu/" + nokapesertakontrol + "/tglSEP/" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString());
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                utc = String.valueOf(api.GetUTCdatetimeAsString());
                headers.add("X-Timestamp", utc);
                headers.add("X-Signature", api.getHmac(utc));
                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                requestEntity = new HttpEntity(headers);
                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
                nameNode = root.path("metaData");
//                System.out.println("URL : " + URL);
                peserta = "";
                if (nameNode.path("code").asText().equals("200")) {
                    response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("peserta");
                    KdPenyakit.setText("Z09.8");
                    NmPenyakit.setText("Z09.8 - Follow-up examination after other treatment for other conditions");

                    if (Sequel.cariIsi("SELECT\n"
                            + "	bridging_sep.jnspelayanan\n"
                            + "FROM\n"
                            + "	bridging_sep where bridging_sep.no_sep='" + noSEP + "' ").equals("1")) {
                        NoRujukan.setText(noSEP);
                        TujuanKunjungan.setSelectedIndex(0);
                        FlagProsedur.setSelectedIndex(0);
                        Penunjang.setSelectedIndex(0);
                        AsesmenPoli.setSelectedIndex(0);
                        AsalRujukan.setSelectedIndex(1);
//                        KdPoli.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_poli_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                        NmPoli.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.nm_poli_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                        KdDPJP.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                        NmDPJP.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.nm_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                        KdDPJPLayanan.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                        NmDPJPLayanan.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.nm_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
                        kdpoli.setText(Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", KdPoli.getText()));
                        kodepolireg = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", KdPoli.getText());
                        kodedokterreg = Sequel.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", KdDPJP.getText());
                    }
                    
//                    NoSKDP.setText(noSKDP);
                    Kelas.setSelectedItem(response.path("hakKelas").path("kode").asText() + ". " + response.path("hakKelas").path("keterangan").asText().replaceAll("KELAS", "Kelas"));
                    prb = "";
                    peserta = response.path("jenisPeserta").path("keterangan").asText();
                    NoTelp.setText(response.path("mr").path("noTelepon").asText());
                    TPasien.setText(response.path("nama").asText());
                    NoKartu.setText(response.path("noKartu").asText());
                    TNoRM.setText(Sequel.cariIsi("select pasien.no_rkm_medis from pasien where pasien.no_peserta='" + NoKartu.getText() + "'"));
                    NIK.setText(Sequel.cariIsi("select pasien.no_ktp from pasien where pasien.no_rkm_medis='" + TNoRM.getText() + "'"));
                    JK.setText(response.path("sex").asText());
                    Status.setText(response.path("statusPeserta").path("kode").asText() + " " + response.path("statusPeserta").path("keterangan").asText());
                    TglLahir.setText(response.path("tglLahir").asText());
                    JenisPeserta.setText(response.path("jenisPeserta").path("keterangan").asText());
                    KdPpkRujukan.setText(response.path("provUmum").path("kdProvider").asText());
                    NmPpkRujukan.setText(response.path("provUmum").path("nmProvider").asText());
                    isNumber();
                    Kdpnj.setText("BPJ");
                    nmpnj.setText("BPJS");
                    Catatan.setText("POST MRS | "+inisial_petugas);
                } else {
                    emptTeks();
                    JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                }
            } catch (Exception ex) {
                System.out.println("Notifikasi Peserta : " + ex);
                if (ex.toString().contains("UnknownHostException")) {
                    JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                }
            }

        } else {

            try {
                URL = link + "/Rujukan/Peserta/" + nokapesertakontrol;
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                utc = String.valueOf(api.GetUTCdatetimeAsString());
                headers.add("X-Timestamp", utc);
                headers.add("X-Signature", api.getHmac(utc));
                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                requestEntity = new HttpEntity(headers);
                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
                nameNode = root.path("metaData");
                System.out.println("URL : " + URL);
                peserta = "";
                if (nameNode.path("code").asText().equals("200")) {
                    response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                    kd_poli_rujukan = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText());
                    System.out.println("kd_poli_rujukan "+kd_poli_rujukan);
                    KdPenyakit.setText(response.path("diagnosa").path("kode").asText());
                    NmPenyakit.setText(response.path("diagnosa").path("nama").asText());

                    if (Sequel.cariIsi("SELECT\n"
                            + "	bridging_sep.jnspelayanan\n"
                            + "FROM\n"
                            + "	bridging_sep where bridging_sep.no_sep='" + noSEP + "' ").equals("1")) {
                        NoRujukan.setText(noSEP);
                        TujuanKunjungan.setSelectedIndex(0);
                        FlagProsedur.setSelectedIndex(0);
                        Penunjang.setSelectedIndex(0);
                        AsesmenPoli.setSelectedIndex(0);
                        AsalRujukan.setSelectedIndex(1);
//                        KdPoli.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_poli_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                        NmPoli.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.nm_poli_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                        KdDPJP.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                        NmDPJP.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.nm_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                        KdDPJPLayanan.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                        NmDPJPLayanan.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.nm_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
                        kdpoli.setText(Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", KdPoli.getText()));
                        kodepolireg = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", KdPoli.getText());
                        kodedokterreg = Sequel.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", KdDPJP.getText());
                    } else {
                        NoRujukan.setText(response.path("noKunjungan").asText());
                        TujuanKunjungan.setSelectedIndex(2);
//                        KdPoli.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_poli_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                        NmPoli.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.nm_poli_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                        KdDPJP.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                        NmDPJP.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.nm_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                        KdDPJPLayanan.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                        NmDPJPLayanan.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.nm_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
                        kdpoli.setText(Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", KdPoli.getText()));
                        kodepolireg = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", KdPoli.getText());
                        kodedokterreg = Sequel.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", KdDPJP.getText());
                        FlagProsedur.setSelectedIndex(0);
                        Penunjang.setSelectedIndex(0);
                        AsesmenPoli.setSelectedIndex(5);
                        Valid.SetTgl(TanggalRujuk, response.path("tglKunjungan").asText());
                    }
//                    NoSKDP.setText(noSKDP);
                    Kelas.setSelectedItem(response.path("peserta").path("hakKelas").path("kode").asText() + ". " + response.path("peserta").path("hakKelas").path("keterangan").asText().replaceAll("KELAS", "Kelas"));
                    prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                    peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
                    NoTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                    TPasien.setText(response.path("peserta").path("nama").asText());
                    NoKartu.setText(response.path("peserta").path("noKartu").asText());
                    TNoRM.setText(Sequel.cariIsi("select pasien.no_rkm_medis from pasien where pasien.no_peserta='" + NoKartu.getText() + "'"));
                    NIK.setText(Sequel.cariIsi("select pasien.no_ktp from pasien where pasien.no_rkm_medis='" + TNoRM.getText() + "'"));
                    JK.setText(response.path("peserta").path("sex").asText());
                    Status.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                    TglLahir.setText(response.path("peserta").path("tglLahir").asText());
                    JenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                    KdPpkRujukan.setText(response.path("provPerujuk").path("kode").asText());
                    NmPpkRujukan.setText(response.path("provPerujuk").path("nama").asText());
                    isNumber();
                    Kdpnj.setText("BPJ");
                    nmpnj.setText("BPJS");
                    
                    //get tanggal habis rujukan
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar c = Calendar.getInstance();
                    c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(response.path("tglKunjungan").asText()));
                    c.add(Calendar.DATE, 89); // Adding 5 days
                    String tglHabis = sdf.format(c.getTime());
                
                    Catatan.setText("SKP "+tglHabis+" | "+inisial_petugas);
//                    Catatan.setText("Anjungan Mandiri RS Indriati Boyolali");
                } else {
//                    emptTeks();
////                dispose();
//                    JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                    // ambil dari rujukan RS
                    try {
                        URL = link + "/Rujukan/RS/Peserta/" + nokapesertakontrol;
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                        utc = String.valueOf(api.GetUTCdatetimeAsString());
                        headers.add("X-Timestamp", utc);
                        headers.add("X-Signature", api.getHmac(utc));
                        headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                        requestEntity = new HttpEntity(headers);
                        root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
                        nameNode = root.path("metaData");
                        System.out.println("URL : " + URL);
                        peserta = "";
                        if (nameNode.path("code").asText().equals("200")) {
                            response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                            kd_poli_rujukan = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", response.path("poliRujukan").path("kode").asText());
                            System.out.println("kd_poli_rujukan2 "+kd_poli_rujukan);
                            KdPenyakit.setText(response.path("diagnosa").path("kode").asText());
                            NmPenyakit.setText(response.path("diagnosa").path("nama").asText());

                            if (Sequel.cariIsi("SELECT\n"
                                    + "	bridging_sep.jnspelayanan\n"
                                    + "FROM\n"
                                    + "	bridging_sep where bridging_sep.no_sep='" + noSEP + "' ").equals("1")) {
                                NoRujukan.setText(noSEP);
                                TujuanKunjungan.setSelectedIndex(0);
                                FlagProsedur.setSelectedIndex(0);
                                Penunjang.setSelectedIndex(0);
                                AsesmenPoli.setSelectedIndex(0);
                                AsalRujukan.setSelectedIndex(1);
//                                KdPoli.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_poli_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                                NmPoli.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.nm_poli_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                                KdDPJP.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                                NmDPJP.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.nm_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                                KdDPJPLayanan.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                                NmDPJPLayanan.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.nm_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
                                kdpoli.setText(Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", KdPoli.getText()));
                                kodepolireg = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", KdPoli.getText());
                                kodedokterreg = Sequel.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", KdDPJP.getText());
                            } else {
                                NoRujukan.setText(response.path("noKunjungan").asText());
                                TujuanKunjungan.setSelectedIndex(2);
//                                KdPoli.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_poli_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                                NmPoli.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.nm_poli_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                                KdDPJP.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                                NmDPJP.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.nm_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                                KdDPJPLayanan.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.kd_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
//                                NmDPJPLayanan.setText(Sequel.cariIsi("select bridging_surat_kontrol_bpjs.nm_dokter_bpjs from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + noSKDP + "'"));
                                kdpoli.setText(Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", KdPoli.getText()));
                                kodepolireg = Sequel.cariIsi("select kd_poli_rs from maping_poli_bpjs where kd_poli_bpjs=?", KdPoli.getText());
                                kodedokterreg = Sequel.cariIsi("select kd_dokter from maping_dokter_dpjpvclaim where kd_dokter_bpjs=?", KdDPJP.getText());
                                FlagProsedur.setSelectedIndex(0);
                                Penunjang.setSelectedIndex(0);
                                AsesmenPoli.setSelectedIndex(5);
                                Valid.SetTgl(TanggalRujuk, response.path("tglKunjungan").asText());
                            }
//                            NoSKDP.setText(noSKDP);
                            Kelas.setSelectedItem(response.path("peserta").path("hakKelas").path("kode").asText() + ". " + response.path("peserta").path("hakKelas").path("keterangan").asText().replaceAll("KELAS", "Kelas"));
                            prb = response.path("peserta").path("informasi").path("prolanisPRB").asText().replaceAll("null", "");
                            peserta = response.path("peserta").path("jenisPeserta").path("keterangan").asText();
                            NoTelp.setText(response.path("peserta").path("mr").path("noTelepon").asText());
                            TPasien.setText(response.path("peserta").path("nama").asText());
                            NoKartu.setText(response.path("peserta").path("noKartu").asText());
                            TNoRM.setText(Sequel.cariIsi("select pasien.no_rkm_medis from pasien where pasien.no_peserta='" + NoKartu.getText() + "'"));
                            NIK.setText(Sequel.cariIsi("select pasien.no_ktp from pasien where pasien.no_rkm_medis='" + TNoRM.getText() + "'"));
                            JK.setText(response.path("peserta").path("sex").asText());
                            Status.setText(response.path("peserta").path("statusPeserta").path("kode").asText() + " " + response.path("peserta").path("statusPeserta").path("keterangan").asText());
                            TglLahir.setText(response.path("peserta").path("tglLahir").asText());
                            JenisPeserta.setText(response.path("peserta").path("jenisPeserta").path("keterangan").asText());
                            KdPpkRujukan.setText(response.path("provPerujuk").path("kode").asText());
                            NmPpkRujukan.setText(response.path("provPerujuk").path("nama").asText());
                            isNumber();
                            Kdpnj.setText("BPJ");
                            nmpnj.setText("BPJS");

                            //get tanggal habis rujukan
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            Calendar c = Calendar.getInstance();
                            c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(response.path("tglKunjungan").asText()));
                            c.add(Calendar.DATE, 89); // Adding 5 days
                            String tglHabis = sdf.format(c.getTime());

                            Catatan.setText("SKP "+tglHabis+" | "+inisial_petugas);
        //                    Catatan.setText("Anjungan Mandiri RS Indriati Boyolali");
                        } else {
                            if(Sequel.cariInteger("SELECT COUNT(*) FROM bridging_surat_kontrol_bpjs WHERE no_surat = '"+noSKDP+"'") > 0){
                                BPJSCekNoKartu cekViaBPJSKartu=new BPJSCekNoKartu();
                                no_peserta = Sequel.cariIsi("SELECT no_kartu FROM bridging_sep WHERE no_sep = (SELECT no_sep FROM bridging_surat_kontrol_bpjs WHERE no_surat = '"+noSKDP+"')");
                                cekViaBPJSKartu.tampil(no_peserta); 
                                if(cekViaBPJSKartu.informasi.equals("OK")){
                                    if(cekViaBPJSKartu.statusPesertaketerangan.equals("AKTIF")){
                                        
                                        TPasien.setText(cekViaBPJSKartu.nama);
                                        TglLahir.setText(cekViaBPJSKartu.tglLahir);
                                        NIK.setText(cekViaBPJSKartu.nik);
                                        NoKartu.setText(no_peserta);
                                        TNoRM.setText(Sequel.cariIsi("select pasien.no_rkm_medis from pasien where pasien.no_peserta='" + NoKartu.getText() + "'"));
                                        if(NIK.getText().equals("")){
                                            NIK.setText(Sequel.cariIsi("select pasien.no_ktp from pasien where pasien.no_rkm_medis=?",TNoRM.getText()));
                                        }
                                        JK.setText(cekViaBPJSKartu.sex);
                                        JenisPeserta.setText(cekViaBPJSKartu.jenisPesertaketerangan);
                                        Status.setText(cekViaBPJSKartu.statusPesertaketerangan);
                                        KdPpkRujukan.setText(cekViaBPJSKartu.provUmumkdProvider);
                                        NmPpkRujukan.setText(cekViaBPJSKartu.provUmumnmProvider);
                                        if(cekViaBPJSKartu.hakKelaskode.equals("1")){
                                            Kelas.setSelectedIndex(0);
                                        }else if(cekViaBPJSKartu.hakKelaskode.equals("2")){
                                            Kelas.setSelectedIndex(1);
                                        }else if(cekViaBPJSKartu.hakKelaskode.equals("3")){
                                            Kelas.setSelectedIndex(2);
                                        }
                                        NoTelp.setText(cekViaBPJSKartu.mrnoTelepon);
                                        prb=cekViaBPJSKartu.informasiprolanisPRB.replaceAll("null","");
                                        peserta = Sequel.cariIsi("SELECT peserta FROM bridging_sep WHERE no_sep = (SELECT no_sep FROM bridging_surat_kontrol_bpjs WHERE no_surat = '"+noSKDP+"')");
                                        KdPenyakit.setText(Sequel.cariIsi("SELECT diagawal FROM bridging_sep WHERE no_sep = (SELECT no_sep FROM bridging_surat_kontrol_bpjs WHERE no_surat = '"+noSKDP+"')"));
                                        NmPenyakit.setText(Sequel.cariIsi("SELECT nmdiagnosaawal FROM bridging_sep WHERE no_sep = (SELECT no_sep FROM bridging_surat_kontrol_bpjs WHERE no_surat = '"+noSKDP+"')"));
                                        NoRujukan.requestFocus();   
                                        Kdpnj.setText("BPJ");
                                        nmpnj.setText("BPJS");   
                                                                              
                                    }else{
                                        JOptionPane.showMessageDialog(null,"Status kepesertaan tidak aktif..!!");
                                        dispose();
                                    }
                                }else{
                                    emptTeks();
                                    JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                                    dispose();
                                }  
                            }
                            
//                            emptTeks();
//        //                dispose();
//                            JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                        }
                    } catch (Exception ex) {
                        System.out.println("Notifikasi Peserta : " + ex);
                        if (ex.toString().contains("UnknownHostException")) {
                            JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("Notifikasi Peserta : " + ex);
                if (ex.toString().contains("UnknownHostException")) {
                    JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                }
            }
        }
    }

    public void SimpanAntrianOnSite() {
        if ((!NoRujukan.getText().equals("")) || (!NoSKDP.getText().equals(""))) {
            TulisLog("Mulai fungsi SimpanAntrianOnSite");
            if(TujuanKunjungan.getSelectedItem().toString().trim().equals("0. Normal")&&FlagProsedur.getSelectedItem().toString().trim().equals("")&&Penunjang.getSelectedItem().toString().trim().equals("")&&AsesmenPoli.getSelectedItem().toString().trim().equals("")){
                if(AsalRujukan.getSelectedIndex()==0){
                    jeniskunjungan="1";
                }else{
                    if(!NoSKDP.getText().equals("")){
                        jeniskunjungan="3";
                    }else{
                        jeniskunjungan="4";
                    }
                }
            }else if(TujuanKunjungan.getSelectedItem().toString().trim().equals("2. Konsul Dokter")&&FlagProsedur.getSelectedItem().toString().trim().equals("")&&Penunjang.getSelectedItem().toString().trim().equals("")&&AsesmenPoli.getSelectedItem().toString().trim().equals("5. Tujuan Kontrol")){
                jeniskunjungan="3";
            }else if(TujuanKunjungan.getSelectedItem().toString().trim().equals("0. Normal")&&FlagProsedur.getSelectedItem().toString().trim().equals("")&&Penunjang.getSelectedItem().toString().trim().equals("")&&AsesmenPoli.getSelectedItem().toString().trim().equals("4. Atas Instruksi RS")){
                jeniskunjungan="2";
            }else{
                if(TujuanKunjungan.getSelectedItem().toString().trim().equals("2. Konsul Dokter")&&AsesmenPoli.getSelectedItem().toString().trim().equals("5. Tujuan Kontrol")){
                    jeniskunjungan="3";
                }else{
                    jeniskunjungan="2";
                }
            }
            
            try {
                day = cal.get(Calendar.DAY_OF_WEEK);
                switch (day) {
                    case 1:
                        hari = "AKHAD";
                        break;
                    case 2:
                        hari = "SENIN";
                        break;
                    case 3:
                        hari = "SELASA";
                        break;
                    case 4:
                        hari = "RABU";
                        break;
                    case 5:
                        hari = "KAMIS";
                        break;
                    case 6:
                        hari = "JUMAT";
                        break;
                    case 7:
                        hari = "SABTU";
                        break;
                    default:
                        break;
                }

//                ps=koneksi.prepareStatement("select jadwal.jam_mulai,jadwal.jam_selesai,jadwal.kuota from jadwal where jadwal.hari_kerja=? and jadwal.kd_poli=? and jadwal.kd_dokter=?");
                ps=koneksi.prepareStatement("select jadwal.jam_mulai,jadwal.jam_selesai,jadwal.kuota from jadwal where jadwal.hari_kerja=? and jadwal.kd_dokter=?");
                try {
                    ps.setString(1,hari);
//                    ps.setString(2,kodepolireg);
                    ps.setString(2,kodedokterreg);
                    rs=ps.executeQuery();
                    if(rs.next()){
                        jammulai=rs.getString("jam_mulai");
                        jamselesai=rs.getString("jam_selesai");
                        kuota=rs.getInt("kuota");
                        datajam=Sequel.cariIsi("select DATE_ADD(concat('"+Valid.SetTgl(TanggalSEP.getSelectedItem()+"")+"',' ','"+jammulai+"'),INTERVAL "+(Integer.parseInt(NoReg.getText())*10)+" MINUTE) ");
                        parsedDate = dateFormat.parse(datajam);
                    }else{
                        System.out.println("Jadwal tidak ditemukan...!");
                    }
                } catch (Exception e) {
                    System.out.println("Notif : "+e);
                } finally{
                    if(rs!=null){
                        rs.close();
                    }
                    if(ps!=null){
                        ps.close();
                    }
                }   
                
                String kodebooking = "";
                if(NoTelp.getText().length() > 13){
                    NoTelp.setText(NoTelp.getText().substring(0, 12));
                }
                
//                if(checkinMJKN){
//                    //update checkin referensi mobilejkn
//                    kodebooking = Sequel.cariIsi("SELECT nobooking FROM referensi_mobilejkn_bpjs WHERE nomorreferensi = '"+NoRujukan.getText()+"' ORDER BY tanggalperiksa DESC LIMIT 1");
//                    Sequel.mengedit("referensi_mobilejkn_bpjs","nobooking='"+kodebooking+"'","status='Checkin',validasi=now()");
//                }else{
                    //simpan referensi mobilejkn
                    if(Sequel.cariInteger("Select count(*) from referensi_mobilejkn_bpjs where no_rawat = ?",TNoRw.getText()) > 0){
                        kodebooking = Sequel.cariIsi("Select nobooking from referensi_mobilejkn_bpjs where no_rawat = ?",TNoRw.getText());
                        TulisLog("kodebooking sudah ada, "+kodebooking+" "+Sequel.cariInteger("Select count(*) from referensi_mobilejkn_bpjs where nobooking = ?",kodebooking));
                    } else {
                        kodebooking = Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(nobooking,6),signed)),0)+1 from referensi_mobilejkn_bpjs where tanggalperiksa='" + Valid.SetTgl(TanggalSEP.getSelectedItem().toString()) + "' ", Valid.SetTgl(TanggalSEP.getSelectedItem().toString()).replaceAll("-", "") + "", 6);
                        Sequel.menyimpan("referensi_mobilejkn_bpjs", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", 23, new String[]{
                            kodebooking,TNoRw.getText(),NoKartu.getText(),NIK.getText(),NoTelp.getText(),KdPoli.getText(),(statuspasien == "Baru")? "1" : "0",TNoRM.getText(),
                            Valid.SetTgl(TanggalSEP.getSelectedItem() + ""),KdDPJP.getText(),jammulai.substring(0, 5) + "-" + jamselesai.substring(0, 5),
                            jenisKunjungan,nomorReferensi,kodepolireg+"-"+NoReg.getText(),NoReg.getText(),parsedDate.getTime()+"",(kuota - Integer.parseInt(NoReg.getText()))+"",
                            kuota+"",(kuota - Integer.parseInt(NoReg.getText()))+"",kuota+"","Checkin",new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date()),"Belum"
                        });
                        TulisLog("Menyimpan referensi_mobilejkn_bpjs "+kodebooking+"|"+TNoRw.getText()+"|"+NoKartu.getText()+"|"+NIK.getText()+"|"+NoTelp.getText()+"|"+KdPoli.getText()+"|"+((statuspasien == "Baru")? "1" : "0")+"|"+TNoRM.getText()+"|"+
                        Valid.SetTgl(TanggalSEP.getSelectedItem() + "")+"|"+KdDPJP.getText()+"|"+jammulai.substring(0, 5) + "-" + jamselesai.substring(0, 5)+"|"+
                        jenisKunjungan+"|"+nomorReferensi+"|"+kodepolireg+"-"+NoReg.getText()+"|"+NoReg.getText()+"|"+parsedDate.getTime()+""+"|"+(kuota - Integer.parseInt(NoReg.getText()))+""+"|"+
                        kuota+""+"|"+(kuota - Integer.parseInt(NoReg.getText()))+""+"|"+kuota+""+"|"+"Checkin"+"|"+new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date())+"|"+"Belum");

                        TulisLog("kodebooking "+kodebooking+" "+Sequel.cariInteger("Select count(*) from referensi_mobilejkn_bpjs where nobooking = ?",kodebooking));
                    }
                
//                }
                
//                if (!NoSKDP.getText().equals("")) {
                String respon="200";
                TulisLog("Nomor Referensi "+nomorReferensi);
                if (nomorReferensi != "") {
                    try {
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
                        utc = String.valueOf(api.GetUTCdatetimeAsString());
                        headers.add("x-timestamp", utc);
                        headers.add("x-signature", api.getHmac(utc));
                        headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());

                        requestJson = "{"
                                + "\"kodebooking\": \"" + kodebooking + "\","
                                + "\"jenispasien\": \"JKN\","
                                + "\"nomorkartu\": \"" + NoKartu.getText() + "\","
                                + "\"nik\": \"" + NIK.getText() + "\","
                                + "\"nohp\": \"" + NoTelp.getText() + "\","
                                + "\"kodepoli\": \"" + KdPoli.getText() + "\","
                                + "\"namapoli\": \"" + NmPoli.getText() + "\","
                                + "\"pasienbaru\": 0,"
                                + "\"norm\": \"" + TNoRM.getText() + "\","
                                + "\"tanggalperiksa\": \"" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "\","
                                + "\"kodedokter\": " + KdDPJP.getText() + ","
                                + "\"namadokter\": \"" + NmDPJP.getText() + "\","
                                + "\"jampraktek\": \"" + jammulai.substring(0, 5) + "-" + jamselesai.substring(0, 5) + "\","
                                + "\"jeniskunjungan\": " + jeniskunjungan + ","
                                + "\"nomorreferensi\": \"" + nomorReferensi + "\"," //NoSKDP.getText().toString()
                                + "\"nomorantrean\": \"" + NoReg.getText() + "\","
                                + "\"angkaantrean\": " + Integer.parseInt(NoReg.getText()) + ","
                                + "\"estimasidilayani\": " + parsedDate.getTime() + ","
                                + "\"sisakuotajkn\": " + (kuota - Integer.parseInt(NoReg.getText())) + ","
                                + "\"kuotajkn\": " + kuota + ","
                                + "\"sisakuotanonjkn\": " + (kuota - Integer.parseInt(NoReg.getText())) + ","
                                + "\"kuotanonjkn\": " + kuota + ","
                                + "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi. Estimasi pelayanan 10 menit per pasien\""
                                + "}";
                        requestEntity = new HttpEntity(requestJson, headers);
                        URL = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
                        TulisLog("WS Add Antrean JSON: "+requestJson+" \n URL: "+URL+" \n headers: "+headers);
                        root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class
                        ).getBody());
                        nameNode = root.path("metadata");
                        respon=nameNode.path("code").asText();
                        if (nameNode.path("code").asText().equals("200")) {
                            
                            TulisLog("WS Add Antrean berhasil 1, update status kirim menjadi Sudah "+kodebooking);
                            Sequel.queryu2("update referensi_mobilejkn_bpjs set statuskirim='Sudah' where nobooking='"+kodebooking+"'");
//                            Sequel.menyimpan2("referensi_mobilejkn_bpjs_taskid_status200", "?,?,?,?,?,?,?,?", 8, new String[]{
//                                TNoRw.getText(), "0000-00-00 00:00:00", "0000-00-00 00:00:00", "0000-00-00 00:00:00", "0000-00-00 00:00:00", "0000-00-00 00:00:00", "0000-00-00 00:00:00", "0000-00-00 00:00:00"
//                            });
//                            System.out.println("Menjalankan WS taskid3");
//                            headers2 = new HttpHeaders();
//                            headers2.setContentType(MediaType.APPLICATION_JSON);
//                            headers2.add("x-cons-id",koneksiDB.CONSIDAPIMOBILEJKN());
//                            utc=String.valueOf(api.GetUTCdatetimeAsString());
//                            headers2.add("x-timestamp",utc);
//                            headers2.add("x-signature",api.getHmac(utc));
//                            headers2.add("user_key",koneksiDB.USERKEYAPIMOBILEJKN());
//                            requestJson2 ="{" +
//                                             "\"kodebooking\": \""+kodebooking+"\"," +
//                                             "\"taskid\": \"3\"," +
//                                             "\"waktu\": \""+parsedDate.getTime()+"\"" +
//                                          "}";
//                            System.out.println("JSON : "+requestJson2);
//                            requestEntity2 = new HttpEntity(requestJson2,headers2);
//                            URL = koneksiDB.URLAPIMOBILEJKN() + "/antrean/updatewaktu";	
//                            System.out.println("URL : "+URL);
//                            //System.out.println(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
//                            root2 = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity2, String.class).getBody());
//                            nameNode2 = root2.path("metadata");
//                            if(!nameNode2.path("code").asText().equals("200")){
//                                System.out.println("Update TaskID 3 "+kodebooking+" gagal");
//                                TulisLog("Update TaskID 3 "+ TNoRM.getText()+ " : " +kodebooking+" gagal\n"+"respon WS BPJS : " + nameNode.path("code").asText() + " " + nameNode.path("message").asText() + "\n");
//                            }  
                        } else {
                            System.out.println("Daftar antrean "+kodebooking+" gagal | "+nameNode.path("message").asText());
                            TulisLog("Daftar antrean "+ TNoRM.getText()+ " : " +kodebooking+" gagal\n"+"respon WS BPJS : " + nameNode.path("code").asText() + " " + nameNode.path("message").asText() + ""); 
                            TulisLog("Mencoba NoSKDP "+NoSKDP.getText());
                            if(!NoSKDP.getText().equals("")){
                                try {
                                    headers = new HttpHeaders();
                                    headers.setContentType(MediaType.APPLICATION_JSON);
                                    headers.add("x-cons-id",koneksiDB.CONSIDAPIMOBILEJKN());
                                    utc=String.valueOf(api.GetUTCdatetimeAsString());
                                    headers.add("x-timestamp",utc);
                                    headers.add("x-signature",api.getHmac(utc));
                                    headers.add("user_key",koneksiDB.USERKEYAPIMOBILEJKN());

                                    requestJson ="{" +
                                                    "\"kodebooking\": \""+TNoRw.getText()+"\"," +
                                                    "\"jenispasien\": \"JKN\"," +
                                                    "\"nomorkartu\": \""+NoKartu.getText()+"\"," +
                                                    "\"nik\": \""+NIK.getText()+"\"," +
                                                    "\"nohp\": \""+NoTelp.getText()+"\"," +
                                                    "\"kodepoli\": \""+KdPoli.getText()+"\"," +
                                                    "\"namapoli\": \""+NmPoli.getText()+"\"," +
                                                    "\"pasienbaru\": 0," +
                                                    "\"norm\": \""+TNoRM.getText()+"\"," +
                                                    "\"tanggalperiksa\": \""+Valid.SetTgl(TanggalSEP.getSelectedItem()+"")+"\"," +
                                                    "\"kodedokter\": "+KdDPJP.getText()+"," +
                                                    "\"namadokter\": \""+NmDPJP.getText()+"\"," +
                                                    "\"jampraktek\": \""+jammulai.substring(0,5)+"-"+jamselesai.substring(0,5)+"\"," +
                                                    "\"jeniskunjungan\": "+jeniskunjungan+"," +
                                                    "\"nomorreferensi\": \""+NoSKDP.getText()+"\"," +
                                                    "\"nomorantrean\": \""+NoReg.getText()+"\"," +
                                                    "\"angkaantrean\": "+Integer.parseInt(NoReg.getText())+"," +
                                                    "\"estimasidilayani\": "+parsedDate.getTime()+"," +
                                                    "\"sisakuotajkn\": "+(kuota-Integer.parseInt(NoReg.getText()))+"," +
                                                    "\"kuotajkn\": "+kuota+"," +
                                                    "\"sisakuotanonjkn\": "+(kuota-Integer.parseInt(NoReg.getText()))+"," +
                                                    "\"kuotanonjkn\": "+kuota+"," +
                                                    "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\"" +
                                                "}";
                                    System.out.println("JSON : "+requestJson+"\n");
                                    requestEntity = new HttpEntity(requestJson,headers);
                                    URL = koneksiDB.URLAPIMOBILEJKN()+"/antrean/add";	
                                    TulisLog("WS Add Antrean JSON: "+requestJson+" \n URL: "+URL+" \n headers: "+headers);
                                    root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                    nameNode = root.path("metadata");  
                                    System.out.println("respon WS BPJS Kirim Pakai SKDP : "+nameNode.path("code").asText()+" "+nameNode.path("message").asText()+"\n");
                                    if(nameNode.path("code").asText().equals("200")){
                                        TulisLog("WS Add Antrean berhasil 2, update status kirim menjadi Sudah "+TNoRw.getText());
                                        Sequel.queryu2("update referensi_mobilejkn_bpjs set statuskirim='Sudah' where nobooking='"+TNoRw.getText()+"'");
                                    } else {
                                        System.out.println("Daftar antrean2 "+kodebooking+" gagal | "+nameNode.path("message").asText());
                                        TulisLog("Daftar antrean2 "+ TNoRM.getText()+ " : " +kodebooking+" gagal\n"+"respon WS BPJS : " + nameNode.path("code").asText() + " " + nameNode.path("message").asText() + "\n"); 
                                    }
                                } catch (Exception e) {
                                    System.out.println("Notif SKDP : "+e);
                                }
                            }
                        }
                        System.out.println("respon WS BPJS : " + nameNode.path("code").asText() + " " + nameNode.path("message").asText() + "");
                    } catch (Exception e) {
                        System.out.println("Notif SKDP : " + e);
                    }
                }else{
                
//                if(respon.equals("201")){
                    TulisLog("NoSKDP "+NoSKDP.getText());
                    if(!NoSKDP.getText().equals("")){
                        try {
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("x-cons-id",koneksiDB.CONSIDAPIMOBILEJKN());
                            utc=String.valueOf(api.GetUTCdatetimeAsString());
                            headers.add("x-timestamp",utc);
                            headers.add("x-signature",api.getHmac(utc));
                            headers.add("user_key",koneksiDB.USERKEYAPIMOBILEJKN());

                            requestJson ="{" +
                                            "\"kodebooking\": \""+TNoRw.getText()+"\"," +
                                            "\"jenispasien\": \"JKN\"," +
                                            "\"nomorkartu\": \""+NoKartu.getText()+"\"," +
                                            "\"nik\": \""+NIK.getText()+"\"," +
                                            "\"nohp\": \""+NoTelp.getText()+"\"," +
                                            "\"kodepoli\": \""+KdPoli.getText()+"\"," +
                                            "\"namapoli\": \""+NmPoli.getText()+"\"," +
                                            "\"pasienbaru\": 0," +
                                            "\"norm\": \""+TNoRM.getText()+"\"," +
                                            "\"tanggalperiksa\": \""+Valid.SetTgl(TanggalSEP.getSelectedItem()+"")+"\"," +
                                            "\"kodedokter\": "+KdDPJP.getText()+"," +
                                            "\"namadokter\": \""+NmDPJP.getText()+"\"," +
                                            "\"jampraktek\": \""+jammulai.substring(0,5)+"-"+jamselesai.substring(0,5)+"\"," +
                                            "\"jeniskunjungan\": "+jeniskunjungan+"," +
                                            "\"nomorreferensi\": \""+NoSKDP.getText()+"\"," +
                                            "\"nomorantrean\": \""+NoReg.getText()+"\"," +
                                            "\"angkaantrean\": "+Integer.parseInt(NoReg.getText())+"," +
                                            "\"estimasidilayani\": "+parsedDate.getTime()+"," +
                                            "\"sisakuotajkn\": "+(kuota-Integer.parseInt(NoReg.getText()))+"," +
                                            "\"kuotajkn\": "+kuota+"," +
                                            "\"sisakuotanonjkn\": "+(kuota-Integer.parseInt(NoReg.getText()))+"," +
                                            "\"kuotanonjkn\": "+kuota+"," +
                                            "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\"" +
                                        "}";
                            System.out.println("JSON : "+requestJson+"\n");
                            requestEntity = new HttpEntity(requestJson,headers);
                            URL = koneksiDB.URLAPIMOBILEJKN()+"/antrean/add";	
                            TulisLog("WS Add Antrean JSON: "+requestJson+" \n URL: "+URL+" \n headers: "+headers);
                            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                            nameNode = root.path("metadata");  
                            System.out.println("respon WS BPJS Kirim Pakai SKDP : "+nameNode.path("code").asText()+" "+nameNode.path("message").asText()+"\n");
                            if(nameNode.path("code").asText().equals("200")){
                                TulisLog("WS Add Antrean berhasil 3, update status kirim menjadi Sudah "+TNoRw.getText());
                                Sequel.queryu2("update referensi_mobilejkn_bpjs set statuskirim='Sudah' where nobooking='"+TNoRw.getText()+"'");
                            } else {
                                System.out.println("Daftar antrean2 "+kodebooking+" gagal | "+nameNode.path("message").asText());
                                TulisLog("Daftar antrean2 "+ TNoRM.getText()+ " : " +kodebooking+" gagal\n"+"respon WS BPJS : " + nameNode.path("code").asText() + " " + nameNode.path("message").asText() + "\n"); 
                            }
                        } catch (Exception e) {
                            System.out.println("Notif SKDP : "+e);
                        }
                    }
                }

//                if (!NoRujukan.getText().equals("")) {
//                    try {
//                        headers = new HttpHeaders();
//                        headers.setContentType(MediaType.APPLICATION_JSON);
//                        headers.add("x-cons-id", koneksiDB.CONSIDAPIMOBILEJKN());
//                        utc = String.valueOf(api.GetUTCdatetimeAsString());
//                        headers.add("x-timestamp", utc);
//                        headers.add("x-signature", api.getHmac(utc));
//                        headers.add("user_key", koneksiDB.USERKEYAPIMOBILEJKN());
//                        requestJson = "{"
//                                + "\"kodebooking\": \"" + TNoRw.getText() + "\","
//                                + "\"jenispasien\": \"JKN\","
//                                + "\"nomorkartu\": \"" + NoKartu.getText() + "\","
//                                + "\"nik\": \"" + NIK.getText() + "\","
//                                + "\"nohp\": \"" + NoTelp.getText() + "\","
//                                + "\"kodepoli\": \"" + KdPoli.getText() + "\","
//                                + "\"namapoli\": \"" + NmPoli.getText() + "\","
//                                + "\"pasienbaru\": 0,"
//                                + "\"norm\": \"" + TNoRM.getText() + "\","
//                                + "\"tanggalperiksa\": \"" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "\","
//                                + "\"kodedokter\": " + KdDPJP.getText() + ","
//                                + "\"namadokter\": \"" + NmDPJP.getText() + "\","
//                                + "\"jampraktek\": \"" + jammulai.substring(0, 5) + "-" + jamselesai.substring(0, 5) + "\","
//                                + "\"jeniskunjungan\": " + jeniskunjungan + ","
//                                + "\"nomorreferensi\": \"" + NoRujukan.getText() + "\","
//                                + "\"nomorantrean\": \"" + NoReg.getText() + "\","
//                                + "\"angkaantrean\": " + Integer.parseInt(NoReg.getText()) + ","
//                                + "\"estimasidilayani\": " + parsedDate.getTime() + ","
//                                + "\"sisakuotajkn\": " + (kuota - Integer.parseInt(NoReg.getText())) + ","
//                                + "\"kuotajkn\": " + kuota + ","
//                                + "\"sisakuotanonjkn\": " + (kuota - Integer.parseInt(NoReg.getText())) + ","
//                                + "\"kuotanonjkn\": " + kuota + ","
//                                + "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\""
//                                + "}";
//                        System.out.println("JSON : " + requestJson + "\n");
//                        requestEntity = new HttpEntity(requestJson, headers);
//                        URL = koneksiDB.URLAPIMOBILEJKN() + "/antrean/add";
//                        System.out.println("URL Kirim Pakai No.Rujuk : " + URL);
//                        root
//                                = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class
//                                ).getBody());
//                        nameNode = root.path("metadata");
//                        if (nameNode.path("code").asText().equals("200")) {
//                            Sequel.menyimpan2("referensi_mobilejkn_bpjs_taskid_status200", "?,?,?,?,?,?,?,?", 8, new String[]{
//                                TNoRw.getText(), "0000-00-00 00:00:00", "0000-00-00 00:00:00", "0000-00-00 00:00:00", "0000-00-00 00:00:00", "0000-00-00 00:00:00", "0000-00-00 00:00:00", "0000-00-00 00:00:00"
//                            });
//                        } else {
//                            Sequel.menyimpan2("referensi_mobilejkn_bpjs_taskid_status201", "?,?,?", 3, new String[]{
//                                TNoRw.getText(), nameNode.path("message").asText(), nameNode.path("code").asText()
//                            });
//                        }
//                        System.out.println("respon WS BPJS : " + nameNode.path("code").asText() + " " + nameNode.path("message").asText() + "\n");
//                    } catch (Exception e) {
//                        System.out.println("Notif No.Rujuk : " + e);
//                    }
//                }
                TulisLog("simpan antrian onsite selesai");
            } catch (Exception e) {
                System.out.println("Notif : " + e);
                TulisLog("Catch error SimpanAntrianOnsite : "+e);
            }
        }
    }

    public void SimpanRegistrasi(){
        isNumber();
        if (Sequel.menyimpantf2("reg_periksa", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Rawat", 19,
                new String[]{NoReg.getText(), TNoRw.getText(), Valid.SetTgl(TanggalSEP.getSelectedItem() + ""), Sequel.cariIsi("select current_time()"),
                    kodedokterreg, TNoRM.getText(), kodepolireg, TPngJwb.getText(), TAlmt.getText(), THbngn.getText(), TBiaya.getText(), "Belum",
                    statuspasien, "Ralan", Kdpnj.getText(), umur, sttsumur, "Belum Bayar", status}) == true) {
            SimpanAntrianOnSite();
        } else {
            isNumber();
            if (Sequel.menyimpantf2("reg_periksa", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Rawat", 19,
                    new String[]{NoReg.getText(), TNoRw.getText(), Valid.SetTgl(TanggalSEP.getSelectedItem() + ""), Sequel.cariIsi("select current_time()"),
                        kodedokterreg, TNoRM.getText(), kodepolireg, TPngJwb.getText(), TAlmt.getText(), THbngn.getText(), TBiaya.getText(), "Belum",
                        statuspasien, "Ralan", Kdpnj.getText(), umur, sttsumur, "Belum Bayar", status}) == true) {
                SimpanAntrianOnSite();
            } else {
                isNumber();
                if (Sequel.menyimpantf2("reg_periksa", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Rawat", 19,
                        new String[]{NoReg.getText(), TNoRw.getText(), Valid.SetTgl(TanggalSEP.getSelectedItem() + ""), Sequel.cariIsi("select current_time()"),
                            kodedokterreg, TNoRM.getText(), kodepolireg, TPngJwb.getText(), TAlmt.getText(), THbngn.getText(), TBiaya.getText(), "Belum",
                            statuspasien, "Ralan", Kdpnj.getText(), umur, sttsumur, "Belum Bayar", status}) == true) {
                    SimpanAntrianOnSite();
                } else {
                    JOptionPane.showMessageDialog(rootPane, "Pendaftaran pasien gagal. Hubungi loket untuk SIM manual...!");
                }
            }
        }
    }
    
    private void emptTeks() {
        TPasien.setText("");
        TanggalSEP.setDate(new Date());
        TanggalRujuk.setDate(new Date());
        TglLahir.setText("");
        NoKartu.setText("");
        JenisPeserta.setText("");
        Status.setText("");
        JK.setText("");
        NoRujukan.setText("");
        KdPpkRujukan.setText("");
        NmPpkRujukan.setText("");
        JenisPelayanan.setSelectedIndex(1);
        Catatan.setText("");
        KdPenyakit.setText("");
        NmPenyakit.setText("");
        KdPoli.setText("");
        NmPoli.setText("");
        Kelas.setSelectedIndex(2);
        LakaLantas.setSelectedIndex(0);
        TNoRM.setText("");
        KdDPJP.setText("");
        NmDPJP.setText("");
        Keterangan.setText("");
        NoSEPSuplesi.setText("");
        KdPropinsi.setText("");
        NmPropinsi.setText("");
        KdKabupaten.setText("");
        NmKabupaten.setText("");
        KdKecamatan.setText("");
        NmKecamatan.setText("");
        Katarak.setSelectedIndex(0);
        Suplesi.setSelectedIndex(0);
        TanggalKKL.setDate(new Date());
        TanggalKKL.setEnabled(false);
        Keterangan.setEditable(false);
        TujuanKunjungan.setSelectedIndex(0);
        FlagProsedur.setSelectedIndex(0);
        FlagProsedur.setEnabled(false);
        Penunjang.setSelectedIndex(0);
        Penunjang.setEnabled(false);
        AsesmenPoli.setSelectedIndex(0);
        AsesmenPoli.setEnabled(true);
        KdDPJPLayanan.setText("");
        NmDPJPLayanan.setText("");
        btnDPJPLayanan.setEnabled(true);
        NoRujukan.requestFocus();
        kodepolireg = "";
        kodedokterreg = "";
    }

    public void setCheckin(){
        checkinMJKN = true;
    }
    
    private void isPoli() {
        try {
            ps = koneksi.prepareStatement("select registrasi, registrasilama "
                    + " from poliklinik where kd_poli=? order by nm_poli");
            try {
                ps.setString(1, kodepolireg);
                rs = ps.executeQuery();
                if (rs.next()) {
                    if (statuspasien.equals("Lama")) {
                        TBiaya.setText(rs.getString("registrasilama"));
                    } else if (statuspasien.equals("Baru")) {
                        TBiaya.setText(rs.getString("registrasi"));
                    } else {
                        TBiaya.setText(rs.getString("registrasi"));
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }

                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif Cari Poli : " + e);
        }
    }
    
    public void CekProsedur(){
        if(KdPoli.getText().equals("HDL")){
            TujuanKunjungan.setSelectedIndex(1); //prosedur
            FlagProsedur.setSelectedIndex(2); //terapi berkelanjutan
            Penunjang.setSelectedIndex(12); //HD
            AsesmenPoli.setSelectedIndex(0);
        }else{
            if(KdPoli.getText().equals(kd_poli_rujukan)){
                TujuanKunjungan.setSelectedIndex(2); //konsul dokter
                FlagProsedur.setSelectedIndex(0);
                Penunjang.setSelectedIndex(0);
                AsesmenPoli.setSelectedIndex(5); //tujuan kontrol
            } else if (!KdPoli.getText().equals(kd_poli_rujukan)) {
                TujuanKunjungan.setSelectedIndex(0); //normal
                FlagProsedur.setSelectedIndex(0);
                Penunjang.setSelectedIndex(0);
                AsesmenPoli.setSelectedIndex(2); //jam poli telah berakhir
            }  
        }
    }

    private void CetakSEPOtomatis(String nomorsep) {

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("prb", Sequel.cariIsi("select bpjs_prb.prb from bpjs_prb where bpjs_prb.no_sep=?", nomorsep));
        param.put("logo", Sequel.cariGambar("select gambar.bpjs from gambar"));
        param.put("parameter", nomorsep);
        if (JenisPelayanan.getSelectedIndex() == 0) {
            Valid.MyReport("rptBridgingSEP.jasper", "report", "::[ Cetak SEP ]::", param);
        } else {
            Valid.MyReport("rptBridgingSEP2.jasper", "report", "::[ Cetak SEP ]::", param);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void BukaFingerPrint(String NomorKartu) {
        if (!NoKartu.getText().equals("")) {
            this.toFront();
            try {
                Runtime.getRuntime().exec(urlaplikasi);
                Robot robot = new Robot();
//                StringSelection stringSelection = new StringSelection(urlfinger);
//                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//                clipboard.setContents(stringSelection, stringSelection);
//                Thread.sleep(1000);
//                robot.keyPress(KeyEvent.VK_CONTROL);
//                robot.keyPress(KeyEvent.VK_V);
//                robot.keyRelease(KeyEvent.VK_V);
//                robot.keyRelease(KeyEvent.VK_CONTROL);
//                robot.keyPress(KeyEvent.VK_TAB);
//                robot.keyRelease(KeyEvent.VK_TAB);
//                robot.keyPress(KeyEvent.VK_ENTER);
//                robot.keyRelease(KeyEvent.VK_ENTER);
//                Thread.sleep(1500);
                StringSelection stringSelectionuser = new StringSelection(userfinger);
                Clipboard clipboarduser = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboarduser.setContents(stringSelectionuser, stringSelectionuser);
                Thread.sleep(4000);
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_TAB);
                robot.keyRelease(KeyEvent.VK_TAB);
                Thread.sleep(3000);
                StringSelection stringSelectionpass = new StringSelection(passfinger);
                Clipboard clipboardpass = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboardpass.setContents(stringSelectionpass, stringSelectionpass);
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);
                Thread.sleep(3000);
                StringSelection stringSelectionnokartu = new StringSelection(NoKartu.getText());
                Clipboard clipboardnokartu = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboardnokartu.setContents(stringSelectionnokartu, stringSelectionnokartu);
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_CONTROL);
            } catch (IOException ex) {
                System.out.println("Notif Finger : " + ex);
            } catch (AWTException ex) {
                System.out.println("Notif Finger : " + ex);
            } catch (InterruptedException ex) {
                System.out.println("Notif Finger : " + ex);
            }
        }
    }

    private void UpdateSuratKontrol(String NoSurat, String NoSEPKontrol, String KdDokterKontrol, String KdPoliKontrol, String Tanggalkontrol, String userKontrol) {
        if (!NoSurat.equals("")) {
            String namapoliKontrol = Sequel.cariIsi("select maping_poli_bpjs.nm_poli_bpjs from maping_poli_bpjs where maping_poli_bpjs.kd_poli_bpjs='" + KdPoliKontrol + "'");
            String namadokterkontrol = Sequel.cariIsi("select maping_dokter_dpjpvclaim.nm_dokter_bpjs from maping_dokter_dpjpvclaim where maping_dokter_dpjpvclaim.kd_dokter_bpjs='" + KdDokterKontrol + "'");
            String tanggalsuratkontrol = Sequel.cariIsi("select bridging_surat_kontrol_bpjs.tgl_surat from bridging_surat_kontrol_bpjs where bridging_surat_kontrol_bpjs.no_surat='" + NoSurat + "'");
            try {
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
                utc = String.valueOf(api.GetUTCdatetimeAsString());
                headers.add("X-Timestamp", utc);
                headers.add("X-Signature", api.getHmac(utc));
                headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
                URL = link + "/RencanaKontrol/Update";
                requestJson = "{"
                        + "\"request\": {"
                        + "\"noSuratKontrol\":\"" + NoSurat + "\","
                        + "\"noSEP\":\"" + NoSEPKontrol + "\","
                        + "\"kodeDokter\":\"" + KdDokterKontrol + "\","
                        + "\"poliKontrol\":\"" + KdPoliKontrol + "\","
                        + "\"tglRencanaKontrol\":\"" + Tanggalkontrol + "\","
                        + "\"user\":\"" + userKontrol + "\""
                        + "}"
                        + "}";
                System.out.println("JSON : " + requestJson);
                requestEntity = new HttpEntity(requestJson, headers);
                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.PUT, requestEntity, String.class).getBody());
                nameNode = root.path("metaData");
                System.out.println("code : " + nameNode.path("code").asText());
                System.out.println("message : " + nameNode.path("message").asText());
                if (nameNode.path("code").asText().equals("200")) {
                    if (Sequel.mengedittf("bridging_surat_kontrol_bpjs", "no_surat=?", "tgl_surat=?,tgl_rencana=?,kd_dokter_bpjs=?,nm_dokter_bpjs=?,kd_poli_bpjs=?,nm_poli_bpjs=?", 7, new String[]{
                        tanggalsuratkontrol, Tanggalkontrol, KdDokterKontrol, namadokterkontrol, KdPoliKontrol, namapoliKontrol, NoSurat
                    }) == true) {
                        System.out.println("Respon BPJS : " + nameNode.path("message").asText());
//                        JOptionPane.showMessageDialog(rootPane, "Respon BPJS : "+nameNode.path("message").asText());
                    }
                } else {
                    JOptionPane.showMessageDialog(rootPane, nameNode.path("message").asText());
                }
            } catch (Exception ex) {
                System.out.println("Notifikasi Bridging : " + ex);
                if (ex.toString().contains("UnknownHostException")) {
                    JOptionPane.showMessageDialog(rootPane, "Koneksi ke server BPJS terputus...!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Maaf, Silahkan anda pilih terlebih dulu data yang mau anda ganti...\n Klik data pada table untuk memilih data...!!!!");
        }

    }
    
    private void UpdateSuratKontrolInternal(String NoSurat, String NoSEPKontrol, String KdDokterKontrol, String KdPoliKontrol, String Tanggalkontrol, String userKontrol) {
        if (!NoSurat.equals("")) {
            String namapoliKontrol = Sequel.cariIsi("select maping_poli_bpjs.nm_poli_bpjs from maping_poli_bpjs where maping_poli_bpjs.kd_poli_bpjs='" + KdPoliKontrol + "'");
            String namadokterkontrol = Sequel.cariIsi("select maping_dokter_dpjpvclaim.nm_dokter_bpjs from maping_dokter_dpjpvclaim where maping_dokter_dpjpvclaim.kd_dokter_bpjs='" + KdDokterKontrol + "'");
            String tanggalsuratkontrol = Sequel.cariIsi("select surat_kontrol_internal.tgl_surat from surat_kontrol_internal where surat_kontrol_internal.no_surat='" + NoSurat + "'");
            String idSurat = Sequel.cariIsi("select surat_kontrol_internal.id from surat_kontrol_internal where surat_kontrol_internal.no_surat='" + NoSurat + "'");
            if (Sequel.mengedittf("surat_kontrol_internal", "id=?", "tgl_surat=?,tgl_rencana=?,kd_dokter_bpjs=?,nm_dokter_bpjs=?,kd_poli_bpjs=?,nm_poli_bpjs=?", 7, new String[]{
                    tanggalsuratkontrol, Tanggalkontrol, KdDokterKontrol, namadokterkontrol, KdPoliKontrol, namapoliKontrol, idSurat
                }) == true) {
                    System.out.println("Update surat kontrol internal berhasil..");
//                        JOptionPane.showMessageDialog(rootPane, "Respon BPJS : "+nameNode.path("message").asText());
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Maaf, Silahkan anda pilih terlebih dulu data yang mau anda ganti...\n Klik data pada table untuk memilih data...!!!!");
        }

    }
    
    private long findDifference(String start_date,String end_date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long difference_In_Days=0;
        try {
            Date obj = new Date(); 
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(sdf.format(obj));
            
            System.out.println("start_date "+start_date);
            System.out.println("end_date "+sdf.format(obj));
            // Calculate time difference
            // in milliseconds
            long difference_In_Time = d2.getTime() - d1.getTime();
            difference_In_Days= (difference_In_Time / (1000 * 60 * 60 * 24));
            
        }
 
        // Catch the Exception
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return difference_In_Days;
    }
    
    public void getRujukanKhusus(String nomorkartu){        
        Date today = new Date();
        Calendar cal = Calendar.getInstance(); 
        cal.setTime(today); 
        int month = 0;
        int year = 0;
        String month_str = "";
        tgl_rujukan = "";
//        System.out.println("tanggal rujuk "+TanggalRujuk.getSelectedItem().toString());
        long diff = findDifference(Valid.SetTgl(TanggalRujuk.getSelectedItem().toString()),today.toString());
        System.out.println("diff "+diff);
        if(diff>89){
            JOptionPane.showMessageDialog(rootPane, "Mengambil data rujukan khusus!!!!");
            for(int i = 0; i < 4; i++){
                month = cal.get(Calendar.MONTH) + 1;
                year = cal.get(Calendar.YEAR);
                System.out.println(month+" "+year);
                month_str = (month<10)? "0"+String.valueOf(month) : String.valueOf(month);
                getDataRujukanKhusus(nomorkartu,month_str,String.valueOf(year));
                cal.add(Calendar.MONTH, -1);
            } 
        }
        
    }
    
    public void getDataRujukanKhusus(String nomorkartu, String bulan, String tahun){
        try {
            URL = link+"/Rujukan/Khusus/List/Bulan/"+bulan+"/Tahun/"+tahun;
            headers= new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.add("X-Cons-ID",koneksiDB.CONSIDAPIBPJS());
	    utc=String.valueOf(api.GetUTCdatetimeAsString());
	    headers.add("X-Timestamp",utc);
	    headers.add("X-Signature",api.getHmac(utc));
            headers.add("user_key",koneksiDB.USERKEYAPIBPJS());
	    requestEntity = new HttpEntity(headers);
	    root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");
//            System.out.println("URL : "+URL);
            if(nameNode.path("code").asText().equals("200")){
                response = mapper.readTree(api.Decrypt(root.path("response").asText(),utc)).path("rujukan");
                if(response.isArray()){
                    for(JsonNode list:response){
                        if(list.path("nokapst").asText().equalsIgnoreCase(nomorkartu)){
                            if(tgl_rujukan == ""){
                                System.out.println(list.path("tglrujukan_awal").asText());
                                Valid.SetTgl(TanggalRujuk, list.path("tglrujukan_awal").asText());//get tanggal habis rujukan
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar c = Calendar.getInstance();
                                c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(list.path("tglrujukan_awal").asText()));
                                c.add(Calendar.DATE, 89); // Adding 89 days
                                String tglHabis = sdf.format(c.getTime());
                                Catatan.setText("SKP "+tglHabis+" | "+inisial_petugas);
                                tgl_rujukan = list.path("tglrujukan_awal").asText().toString();
                            }
                        }
                    }
                } 
            }else {
                System.out.println("Rujukan Khusus : "+nameNode.path("message").asText());             
            }   
        } catch (Exception ex) {
            System.out.println("Notifikasi Peserta : "+ex);
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(rootPane,"Koneksi ke server BPJS terputus...!");
            }
        }
    }
    
    public void TulisLog(String log){
        try{
            PrintWriter out = new PrintWriter(new FileWriter("ErrorLog/ErrorLog.txt",true));
            out.println(log+"");
            out.close();
        } catch(Exception e){
            System.out.println(e);
        }
   }
    
//    
//    private String ambilTokenWSRS(){
//        String token = "";
//        try {     
//            headers2 = new HttpHeaders();
//            headers2.setContentType(MediaType.APPLICATION_JSON);
//            headers2.add("x-username","bridging_rstds");
//            headers2.add("x-password","RSTSoepraoen0341");
//            URL = "https://rssoepraoen.com/webapps/api-bpjsfktl/auth";
//            requestEntity2 = new HttpEntity(headers);
//            root2 = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity2, String.class).getBody());
//            nameNode2 = root2.path("metadata");
//            if(nameNode2.path("code").asText().equals("200")){
//                response2 = mapper.readTree(root2.path("response").asText());
//                if(response2.isArray()){
//                    for(JsonNode list:response){
//                        token = list.path("token").asText();
//                    }
//                }
//            }else {
//                System.out.println("Notif : "+nameNode.path("message").asText());   
//                token = "";
//            }  
//            System.out.println("respon WS BPJS : "+nameNode2.path("code").asText()+" "+nameNode2.path("message").asText()+"\n");
//        }catch (Exception ex) {
//            System.out.println("Notifikasi Bridging : "+ex);
//        }
//        return token;
//    }
//    
//    private void ambilAntreanRS(String nokartu, String nik, String nohp, String kodepoli, String norm, String tglperiksa, String kodedokter, String jampraktek, String jnskunjungan, String noref){
//        String token = ambilTokenWSRS();
//        try {     
//            headers2 = new HttpHeaders();
//            headers2.setContentType(MediaType.APPLICATION_JSON);
//            headers2.add("x-token",token);
//            headers2.add("x-username","bridging_rstds");
//            requestJson2 ="{" +
//                            "\"nomorkartu\": \""+nokartu+"\"," +
//                            "\"nik\": \""+nik+"\"," +
//                            "\"nohp\": \""+nohp+"\"," +
//                            "\"kodepoli\": \""+kodepoli+"\"," +
//                            "\"norm\": \""+norm+"\"," +
//                            "\"tanggalperiksa\": \""+tglperiksa+"\"," +
//                            "\"kodedokter\": "+kodedokter+"," +
//                            "\"jampraktek\": \""+jampraktek+"\"," +
//                            "\"jeniskunjungan\": "+jnskunjungan+"," +
//                            "\"nomorreferensi\": \""+noref+"\"," +
//                        "}";
//            System.out.println("JSON : "+requestJson2+"\n");
//            requestEntity2 = new HttpEntity(requestJson2,headers2);
//            URL = "https://rssoepraoen.com/webapps/api-bpjsfktl/ambilantrean ";	
//            System.out.println("URL : "+URL);
//            //System.out.println(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
//            root2 = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity2, String.class).getBody());
//            nameNode2 = root2.path("metadata");
//            if(nameNode2.path("code").asText().equals("200")){
//                System.out.println("Ambil antrean WS RS berhasil..!!");
//            }else{
//                System.out.println("Ambil antrean WS RS gagal..!!");
//                System.out.println("respon WS BPJS : "+nameNode2.path("code").asText()+" "+nameNode2.path("message").asText()+"\n");
//            }
//        }catch (Exception ex) {
//            System.out.println("Notifikasi Bridging : "+ex);
//        }
//    }
//    
//    private void tambahAntreanBPJS(){
//        try {     
//            headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.add("x-cons-id",koneksiDB.CONSIDAPIMOBILEJKN());
//            utc=String.valueOf(api.GetUTCdatetimeAsString());
//            headers.add("x-timestamp",utc);
//            headers.add("x-signature",api.getHmac(utc));
//            headers.add("user_key",koneksiDB.USERKEYAPIMOBILEJKN());
//            requestJson ="{" +
//                            "\"kodebooking\": \""+rs.getString("nobooking")+"\"," +
//                            "\"jenispasien\": \"JKN\"," +
//                            "\"nomorkartu\": \""+rs.getString("nomorkartu")+"\"," +
//                            "\"nik\": \""+rs.getString("nik")+"\"," +
//                            "\"nohp\": \""+rs.getString("nohp")+"\"," +
//                            "\"kodepoli\": \""+rs.getString("kodepoli")+"\"," +
//                            "\"namapoli\": \""+rs.getString("nm_poli")+"\"," +
//                            "\"pasienbaru\": "+rs.getString("pasienbaru")+"," +
//                            "\"norm\": \""+rs.getString("no_rkm_medis")+"\"," +
//                            "\"tanggalperiksa\": \""+rs.getString("tanggalperiksa")+"\"," +
//                            "\"kodedokter\": "+rs.getString("kodedokter")+"," +
//                            "\"namadokter\": \""+rs.getString("nm_dokter")+"\"," +
//                            "\"jampraktek\": \""+rs.getString("jampraktek")+"\"," +
//                            "\"jeniskunjungan\": "+rs.getString("jeniskunjungan").substring(0,1)+"," +
//                            "\"nomorreferensi\": \""+rs.getString("nomorreferensi")+"\"," +
//                            "\"nomorantrean\": \""+rs.getString("nomorantrean")+"\"," +
//                            "\"angkaantrean\": "+Integer.parseInt(rs.getString("angkaantrean"))+"," +
//                            "\"estimasidilayani\": "+rs.getString("estimasidilayani")+"," +
//                            "\"sisakuotajkn\": "+rs.getString("sisakuotajkn")+"," +
//                            "\"kuotajkn\": "+rs.getString("kuotajkn")+"," +
//                            "\"sisakuotanonjkn\": "+rs.getString("sisakuotanonjkn")+"," +
//                            "\"kuotanonjkn\": "+rs.getString("kuotanonjkn")+"," +
//                            "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\"" +
//                        "}";
//            System.out.println("JSON : "+requestJson+"\n");
//            requestEntity = new HttpEntity(requestJson,headers);
//            URL = link+"/antrean/add";	
//            System.out.println("URL : "+URL);
//            //System.out.println(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
//            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
//            nameNode = root.path("metadata");
//            if(nameNode.path("code").asText().equals("200")||nameNode.path("message").asText().equals("Ok")){
//                Sequel.queryu2("update referensi_mobilejkn_bpjs set statuskirim='Sudah' where nobooking='"+rs.getString("nobooking")+"'");
//            }  
//            System.out.println("respon WS BPJS : "+nameNode.path("code").asText()+" "+nameNode.path("message").asText()+"\n");
//        }catch (Exception ex) {
//            System.out.println("Notifikasi Bridging : "+ex);
//        }
//    }
}
