package com.example.mysoreprintersproject.app.dailycollections

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.CollectionSummaryReport.CollectionSummaryReportActivity
import com.example.mysoreprintersproject.app.CollectionSummaryReport.CollectionSummaryReportAdapter
import com.example.mysoreprintersproject.app.CollectionSummaryReport.Collection_Report_Summary_Fragment
import com.example.mysoreprintersproject.app.DailyWorkSummaryActivity
import com.example.mysoreprintersproject.app.SplashScreenActivity
import com.example.mysoreprintersproject.app.attendance.AttendanceActivity
import com.example.mysoreprintersproject.app.collection_performance.CollectionPerformanceActivity
import com.example.mysoreprintersproject.app.dailyworkingsummryfragment.DailyWorkingSummaryActivity
import com.example.mysoreprintersproject.app.homecontainer.HomeContainerActivity
import com.example.mysoreprintersproject.app.netsale.NetSaleActivity
import com.example.mysoreprintersproject.app.netsale.NetSaleAdapter
import com.example.mysoreprintersproject.app.supplyreport.SupplyReportActivity
import com.example.mysoreprintersproject.network.APIManager
import com.example.mysoreprintersproject.network.SessionManager
import com.example.mysoreprintersproject.responses.CollectionResponses
import com.example.mysoreprintersproject.responses.CollectionSummaryReportResponses
import com.example.mysoreprintersproject.responses.ProfileResponses
import com.google.android.material.navigation.NavigationView
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment
import retrofit2.Call
import retrofit2.Response

class DailyColletionFragment : Fragment() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var sessionManager: SessionManager
    private lateinit var recyclerView:RecyclerView


    private lateinit var searchBar: EditText

    private lateinit var summaryResponses: List<CollectionResponses>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_daily_colletion, container, false).apply {
            drawerLayout = findViewById(R.id.drawer_layout)
            val navigationViewIcon: ImageView = findViewById(R.id.imageSettings)
            navigationViewIcon.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }

            navigationView = findViewById(R.id.navigationView)
            setupNavigationDrawer()

            getCollectionReport()
            getExecutiveProfile()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Additional initialization can be done here if needed

        val fromSpinner: Spinner = requireView().findViewById(R.id.spinner_from)
        val toSpinner: Spinner = requireView().findViewById(R.id.spinner_to)
        val exportButton: Button = requireView().findViewById(R.id.export_button)
        exportButton.setOnClickListener {
            exportToPdf()
            // progressBar.visibility = View.VISIBLE
        }


        val fromAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.from_dates,
            R.layout.spinner_item
        )
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fromSpinner.adapter = fromAdapter

        val toAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.to_dates,
            R.layout.spinner_item
        )
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        toSpinner.adapter = toAdapter



        searchBar=requireView().findViewById(R.id.search_bar)


        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchCollectionSummary(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> startActivity(Intent(requireActivity(), HomeContainerActivity::class.java))
                R.id.nav_attendance -> startActivity(Intent(requireActivity(), AttendanceActivity::class.java))
                R.id.nav_work_summary -> startActivity(Intent(requireActivity(), DailyWorkingSummaryActivity::class.java))
                R.id.nav_collections_performance -> startActivity(Intent(requireActivity(), CollectionPerformanceActivity::class.java))
                R.id.nav_collection_summary -> startActivity(Intent(requireActivity(), CollectionSummaryReportActivity::class.java))
                R.id.nav_collections_report -> startActivity(Intent(requireActivity(), DailyCollectionActivity::class.java))
                R.id.nav_daily_work_summary -> startActivity(Intent(requireActivity(),
                    DailyWorkSummaryActivity::class.java))
                R.id.nav_supply_reports -> startActivity(Intent(requireActivity(), SupplyReportActivity::class.java))
                R.id.nav_net_sales_report -> startActivity(Intent(requireActivity(), NetSaleActivity::class.java))
                R.id.nav_logout -> {
                    sessionManager.logout()
                    sessionManager.clearSession()
                    startActivity(Intent(requireActivity(), SplashScreenActivity::class.java))
                    requireActivity().finishAffinity()
                }
                else -> Log.d("NavigationDrawer", "Unhandled item clicked: ${item.itemId}")
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun getCollectionReport() {
        val serviceGenerator = APIManager.apiInterface
        val accessToken = sessionManager.fetchAuthToken()
        val authorization = "Bearer $accessToken"
        val id = sessionManager.fetchUserId()!!

        serviceGenerator.getCollectionReport(authorization, id, "6")
            .enqueue(object : retrofit2.Callback<List<CollectionResponses>> {
                override fun onResponse(call: Call<List<CollectionResponses>>, response: Response<List<CollectionResponses>>) {
                    if (response.isSuccessful) {
                        summaryResponses = response.body()!!
                        recyclerView = requireView().findViewById(R.id.recyclerview)
                        recyclerView.layoutManager = LinearLayoutManager(
                            requireActivity(),
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        recyclerView.adapter = DayCollectionAdapter(summaryResponses)
                    }else {
                        Toast.makeText(requireActivity(), "Failed to retrieve data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<CollectionResponses>>, t: Throwable) {
                    Log.e("DailyCollectionFragment", "Error fetching data: ${t.message}", t)
                    Toast.makeText(requireActivity(), "Error fetching data: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun searchCollectionSummary(query: String) {
        if (!::summaryResponses.isInitialized) {
           // Toast.makeText(requireActivity(), "", Toast.LENGTH_SHORT).show()
            return
        }

        val filteredList = summaryResponses.filter { summary ->
            summary.agent!!.contains(query, ignoreCase = true) ||
                    summary.month!!.contains(query, ignoreCase = true) ||
                    summary.billAmount!!.contains(query, ignoreCase = true) ||
                    summary.otherAdjustment.toString().contains(query, ignoreCase = true) ||
                    summary.amountCollected.toString().contains(query, ignoreCase = true) ||
                    summary.totalDues.toString().contains(query, ignoreCase = true) ||
                    summary.balanceAmount.toString().contains(query, ignoreCase = true)
        }

        recyclerView.adapter = DayCollectionAdapter(filteredList)
    }
    private fun getExecutiveProfile() {
        val serviceGenerator = APIManager.apiInterface
        val accessToken = sessionManager.fetchAuthToken()
        val authorization = "Bearer $accessToken"
        val id = sessionManager.fetchUserId()!!

        serviceGenerator.getProfileOfExecutive(authorization, id.toInt())
            .enqueue(object : retrofit2.Callback<ProfileResponses> {
                override fun onResponse(call: Call<ProfileResponses>, response: Response<ProfileResponses>) {
                    response.body()?.profileImage?.let { image ->
                        val profileImage: ImageView = requireView().findViewById(R.id.imageSettings2)
                        val file = APIManager.getImageUrl(image)
                        Glide.with(requireActivity()).load(file).into(profileImage)
                    }
                }

                override fun onFailure(call: Call<ProfileResponses>, t: Throwable) {
                    Toast.makeText(requireActivity(), "Error fetching profile data", Toast.LENGTH_SHORT).show()
                }
            })
    }



    @RequiresApi(Build.VERSION_CODES.Q)
    private fun exportToPdf() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        } else {
            generatePdf()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (!isNotificationPermissionGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                Collection_Report_Summary_Fragment.REQUEST_CODE_NOTIFICATION_PERMISSION
            )
        } else {
            generatePdf() // Permission is already granted, proceed with PDF generation
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Notification permission is not required below Android 13
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == com.example.mysoreprintersproject.app.CollectionSummaryReport.Collection_Report_Summary_Fragment.REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generatePdf() // Permission granted, generate the PDF
            } else {
                showNotificationPermissionDeniedDialog() // Permission denied, show dialog
            }
        }
    }


    private fun showNotificationPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Notification Permission Required")
            .setMessage("This app needs notification permission to notify you when the PDF is generated. Please enable it in settings.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                openAppSettings()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }


    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun generatePdf() {
        val resolver = requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "collection_report.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri == null) {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Error creating file", Toast.LENGTH_SHORT).show()
            }
            return
        }

        try {
            val outputStream = resolver.openOutputStream(uri)
            val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(com.itextpdf.kernel.pdf.PdfWriter(outputStream))
            val document = com.itextpdf.layout.Document(pdfDocument)

            val font = PdfFontFactory.createFont(StandardFonts.HELVETICA)
            val title = Paragraph("Collection Report")
                .setFont(font)
                .setFontSize(20f)
                .setTextAlignment(TextAlignment.CENTER)
            document.add(title)
            document.add(Paragraph("\n").setFont(font))

            val adapter = recyclerView.adapter as DayCollectionAdapter
            val summaryList = adapter.getCollectionList()

            // Define a table with the number of columns matching the fields
            val table = com.itextpdf.layout.element.Table(floatArrayOf(1f, 2f, 2f, 2f,2f,2f,2f)).apply {
                setWidth(com.itextpdf.layout.property.UnitValue.createPercentValue(100f))
            }

            // Add table headers
            table.addHeaderCell(Cell().add(Paragraph("Agent Name").setFont(font)))
            table.addHeaderCell(Cell().add(Paragraph("Month").setFont(font)))
            table.addHeaderCell(Cell().add(Paragraph("Bill Amount").setFont(font)))
            table.addHeaderCell(Cell().add(Paragraph("Other Adjustment").setFont(font)))
            table.addHeaderCell(Cell().add(Paragraph("Amount Collected").setFont(font)))
            table.addHeaderCell(Cell().add(Paragraph("Total Dues").setFont(font)))
            table.addHeaderCell(Cell().add(Paragraph("Balance Amount").setFont(font)))

            // Add the summary data to the table
            summaryList.forEach { summary ->
                table.addCell(Cell().add(Paragraph(summary.agent).setFont(font)))
                table.addCell(Cell().add(Paragraph(summary.month).setFont(font)))
                table.addCell(Cell().add(Paragraph(summary.billAmount).setFont(font)))
                table.addCell(Cell().add(Paragraph(summary.otherAdjustment.toString()).setFont(font)))
                table.addCell(Cell().add(Paragraph(summary.amountCollected).setFont(font)))
                table.addCell(Cell().add(Paragraph(summary.totalDues).setFont(font)))
                table.addCell(Cell().add(Paragraph(summary.balanceAmount).setFont(font)))
            }

            document.add(table) // Add the table to the document

            document.close()
            outputStream?.close()

            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "PDF generated successfully", Toast.LENGTH_SHORT).show()
                showNotification(uri)

            }

        } catch (e: Exception) {
            e.printStackTrace()
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Error generating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }








    private fun showNotification(uri: Uri) {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1
        val channelId = "pdf_download_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "PDF Downloads",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for PDF downloads"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.logo) // Ensure this icon resource exists
            .setContentTitle("PDF Downloaded")
            .setContentText("Tap to open")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)

        Log.d("Collection_Report", "Notification displayed for PDF: $uri")
    }


}
