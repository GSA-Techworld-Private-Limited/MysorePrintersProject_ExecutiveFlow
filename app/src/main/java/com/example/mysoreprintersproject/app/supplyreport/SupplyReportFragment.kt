package com.example.mysoreprintersproject.app.supplyreport

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
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
import com.example.mysoreprintersproject.app.dailycollections.DailyCollectionActivity
import com.example.mysoreprintersproject.app.dailyworkingsummryfragment.DailyWorkingSummaryActivity
import com.example.mysoreprintersproject.app.dailyworkingsummryfragment.DailyWorkingSummaryAdapter
import com.example.mysoreprintersproject.app.homecontainer.HomeContainerActivity
import com.example.mysoreprintersproject.app.lprmanagement.LPRManagementActivity
import com.example.mysoreprintersproject.app.netsale.NetSaleActivity
import com.example.mysoreprintersproject.app.notification.NotificationActivity
import com.example.mysoreprintersproject.network.APIManager
import com.example.mysoreprintersproject.network.SessionManager
import com.example.mysoreprintersproject.responses.CollectionSummaryReportResponses
import com.example.mysoreprintersproject.responses.ProfileResponses
import com.example.mysoreprintersproject.responses.SupplyReportResponse
import com.google.android.material.navigation.NavigationView
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment
import retrofit2.Call
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SupplyReportFragment : Fragment() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var sessionManager: SessionManager

    private lateinit var recyclerView:RecyclerView

    private lateinit var searchBar: EditText

    private lateinit var summaryResponses: List<SupplyReportResponse>
    private lateinit var notificationIcon:ImageView

    private var selectedFromDate: String? = null
    private var selectedToDate: String? = null
    private var selectedSegment: String? = null

    private lateinit var progressBar: ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_supply_report, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sessionManager = SessionManager(requireActivity())

        drawerLayout = requireView().findViewById(R.id.drawer_layout)
        val navigatioViewIcon: ImageView = requireView().findViewById(R.id.imageSettings)
        navigationView = requireView().findViewById(R.id.navigationView)
        notificationIcon=requireView().findViewById(R.id.imageSettings1)

        progressBar=requireView().findViewById(R.id.progressBar)
        navigatioViewIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        notificationIcon.setOnClickListener {
            val i=Intent(requireActivity(),NotificationActivity::class.java)
            startActivity(i)
        }

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> startActivity(Intent(requireActivity(), HomeContainerActivity::class.java))
                R.id.nav_attendance -> startActivity(Intent(requireActivity(), AttendanceActivity::class.java))
                R.id.nav_work_summary -> startActivity(Intent(requireActivity(), DailyWorkingSummaryActivity::class.java))
                R.id.nav_collections_performance -> startActivity(Intent(requireActivity(), CollectionPerformanceActivity::class.java))
                R.id.nav_collection_summary -> startActivity(Intent(requireActivity(),
                    CollectionSummaryReportActivity::class.java))
                R.id.nav_daily_work_summary -> startActivity(Intent(requireActivity(),
                    DailyWorkSummaryActivity::class.java))
                R.id.nav_lprmanagement -> startActivity(Intent(requireActivity(),
                    LPRManagementActivity::class.java))
                R.id.nav_collections_report -> startActivity(Intent(requireActivity(), DailyCollectionActivity::class.java))
                R.id.nav_supply_reports -> startActivity(Intent(requireActivity(), SupplyReportActivity::class.java))
                R.id.nav_net_sales_report -> startActivity(Intent(requireActivity(), NetSaleActivity::class.java))
                R.id.nav_notifications -> startActivity(Intent(requireActivity(),
                    NotificationActivity::class.java))
                R.id.nav_logout ->{
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


        val userType = sessionManager.fetchUserRole() // Fetch user type
        val headerView = navigationView.getHeaderView(0) // Get the header view
        val headerTitle: TextView = headerView.findViewById(R.id.nav_header_title) // Assuming you have this TextView in your header layout

// Set the header title based on the user type
        when (userType) {
            "RM" -> headerTitle.text = "Regional Manager"
            "DGM" -> headerTitle.text = "Deputy General Manager"
            "GM" -> headerTitle.text = "General Manager"
        }

        val menu = navigationView.menu

// Hide certain menu items based on the user type
        when (userType) {
            "RM", "DGM", "GM" -> {
                menu.findItem(R.id.nav_lprmanagement).isVisible = false
                menu.findItem(R.id.nav_daily_work_summary).isVisible = false
                menu.findItem(R.id.nav_collections_performance).isVisible = false
            }
        }

        val exportButton: Button = requireView().findViewById(R.id.export_button)
        exportButton.setOnClickListener {
            exportToPdf()
            progressBar.visibility=View.VISIBLE
        }


        searchBar=requireView().findViewById(R.id.search_bar)


        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchCollectionSummary(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        // Setting up spinners with custom layout
        val fromSpinner: EditText = requireView().findViewById(R.id.editText_from)
        val toSpinner: EditText = requireView().findViewById(R.id.editText_to)
        val segmentSpinner: Spinner = requireView().findViewById(R.id.spinner_three)


        // Date format and calendar
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val calendar = Calendar.getInstance()


        // Set up listeners for date pickers and segment spinner
        fromSpinner.setOnClickListener {
            DatePickerDialog(requireActivity(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedFromDate = dateFormat.format(calendar.time)
                fromSpinner.setText(selectedFromDate)
                //filterResponses()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        toSpinner.setOnClickListener {
            DatePickerDialog(requireActivity(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedToDate = dateFormat.format(calendar.time)
                toSpinner.setText(selectedToDate)

                // Show the progress bar
                progressBar.visibility = View.VISIBLE

                // Filter responses on a background thread
                Thread {
                    filterResponses() // This should contain your logic to filter the responses

                    // After filtering is complete, update the UI on the main thread
                    requireActivity().runOnUiThread {
                        // Hide the progress bar after filtering
                        progressBar.visibility = View.GONE
                    }
                }.start() // Start the thread
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }





        val segmentAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.segments,
            R.layout.spinner_item
        )
        segmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        segmentSpinner.adapter = segmentAdapter


        // Set the OnItemSelectedListener for the spinner
        segmentSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedSegment = segmentSpinner.getItemAtPosition(position).toString()

                // Check if the selected segment is the placeholder item
                if (selectedSegment != "Segments") {
                    filterResponses() // Call the method to filter responses based on the selected segment
                    progressBar.visibility=View.VISIBLE
                } else {
                    selectedSegment = "" // Reset selectedSegment to null if the placeholder is selected
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle case when nothing is selected, if necessary
            }
        })


        getSupplyReport()


        getExecutiveProfile()

        progressBar.visibility=View.VISIBLE
    }

    private fun getSupplyReport() {
        val serviceGenerator = APIManager.apiInterface
        val accessToken = sessionManager.fetchAuthToken()
        val authorization = "Bearer $accessToken"
        val id = sessionManager.fetchUserId()!!

        val period = "6"

        serviceGenerator.getSupplyReport(authorization, id, period)
            .enqueue(object : retrofit2.Callback<List<SupplyReportResponse>> {
                override fun onResponse(
                    call: Call<List<SupplyReportResponse>>,
                    response: Response<List<SupplyReportResponse>>
                ) {
                    progressBar.visibility=View.GONE
                    if (response.isSuccessful) {
                        val reports = response.body()
                        if (!reports.isNullOrEmpty()) {
                            summaryResponses = reports.reversed()
                            setupRecyclerView(summaryResponses)
                        } else {
                            // Handle empty data
                            Toast.makeText(requireActivity(), "No data found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireActivity(), "Failed to retrieve data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<SupplyReportResponse>>, t: Throwable) {
                    Log.e("SupplyReportFragment", "Error fetching data: ${t.message}", t)
                    Toast.makeText(requireActivity(), "Error fetching data: ${t.message}", Toast.LENGTH_SHORT).show()
                    progressBar.visibility=View.GONE
                }
            })
    }

    // Update filterResponses() to call getSupplyReportCustomRange() when dates are selected
    private fun filterResponses() {
        if (!selectedFromDate.isNullOrEmpty() && !selectedToDate.isNullOrEmpty()) {
            getSupplyReportCustomRange() // Call this function when both dates are set
        }
    }

    private fun getSupplyReportCustomRange() {
        val serviceGenerator = APIManager.apiInterface
        val accessToken = sessionManager.fetchAuthToken()
        val authorization = "Bearer $accessToken"
        val id = sessionManager.fetchUserId()!!

        val period = "6"

        serviceGenerator.getSupplyReportCustom(authorization,selectedFromDate!!,selectedToDate!!,selectedSegment!!)
            .enqueue(object : retrofit2.Callback<List<SupplyReportResponse>> {
                override fun onResponse(
                    call: Call<List<SupplyReportResponse>>,
                    response: Response<List<SupplyReportResponse>>
                ) {
                    progressBar.visibility=View.GONE
                    if (response.isSuccessful) {
                        val reports = response.body()
                        if (!reports.isNullOrEmpty()) {
                            summaryResponses = reports
                            setupRecyclerView(summaryResponses)
                        } else {
                            // Handle empty data
                            Toast.makeText(requireActivity(), "No data found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireActivity(), "Failed to retrieve data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<SupplyReportResponse>>, t: Throwable) {
                    Log.e("SupplyReportFragment", "Error fetching data: ${t.message}", t)
                    Toast.makeText(requireActivity(), "Error fetching data: ${t.message}", Toast.LENGTH_SHORT).show()
                    progressBar.visibility=View.GONE
                }
            })
    }



    private fun setupRecyclerView(summaryResponses: List<SupplyReportResponse>) {
         recyclerView = requireView().findViewById(R.id.recyclerview)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            adapter = SupplyReportAdapter(summaryResponses)
        }
    }


    private fun searchCollectionSummary(query: String) {
        if (!::summaryResponses.isInitialized) {
            // Toast.makeText(requireActivity(), "", Toast.LENGTH_SHORT).show()
            return
        }

        val filteredList = summaryResponses.filter { summary ->
            summary.Date!!.contains(query, ignoreCase = true) ||
                    summary.SEname!!.contains(query, ignoreCase = true) ||
                    summary.BPcode!!.toString().contains(query, ignoreCase = true) ||
                    summary.Date.toString().contains(query, ignoreCase = true) ||
                    summary.SumofPv.toString().contains(query, ignoreCase = true)
        }

        recyclerView.adapter = SupplyReportAdapter(filteredList)
    }


//    private fun filterResponses() {
//        if (!::summaryResponses.isInitialized) return
//
//        // Log the selected dates for debugging
//        Log.d("FilterResponses", "From Date: $selectedFromDate, To Date: $selectedToDate")
//
//        // Proceed to filter the responses
//        val filteredList = summaryResponses.filter { summary ->
//            val date = summary.Date ?: return@filter false
//
//            // Debugging log to check the summary date
//            Log.d("FilterResponses", "Summary Date: $date")
//
//            // Ensure both from and to dates are not null
//            val isInDateRange = selectedFromDate?.let { from ->
//                selectedToDate?.let { to ->
//                    // Compare using string comparison for "dd-MM-yyyy" format
//                    date in from..to
//                } ?: true // No 'to' date means it doesn't filter by date
//            } ?: true // No 'from' date means it doesn't filter by date
//
//            // Check if the summary's segment matches the selected segment
//            val isSegmentMatch = selectedSegment?.let { segment ->
//                summary.segment.equals(segment, ignoreCase = true) // Case-insensitive comparison
//            } ?: true
//
//            // Return true if both conditions are satisfied
//            isInDateRange || isSegmentMatch
//        }
//
//        // Check if filteredList is empty and log the result
//        Log.d("FilterResponses", "Filtered list size: ${filteredList.size}")
//
//        // Update RecyclerView with the filtered results
//        (recyclerView.adapter as? SupplyReportAdapter)?.updateData(filteredList)
//    }











    private fun getExecutiveProfile() {
        val serviceGenerator = APIManager.apiInterface
        val accessToken = sessionManager.fetchAuthToken()
        val authorization = "Bearer $accessToken"
        val id = sessionManager.fetchUserId()!!

        serviceGenerator.getProfileOfExecutive(authorization, id.toInt())
            .enqueue(object : retrofit2.Callback<ProfileResponses> {
                override fun onResponse(call: Call<ProfileResponses>, response: Response<ProfileResponses>) {
                    progressBar.visibility=View.GONE
                    val profileResponses = response.body()

                    if(profileResponses!=null){

//                        val profileImage:ImageView=requireView().findViewById(R.id.imageSettings2)
//                        val image=profileResponses.profileImage
//                        val file=APIManager.getImageUrl(image!!)
//                        Glide.with(requireActivity()).load(file).into(profileImage)
                    }
                }

                override fun onFailure(call: Call<ProfileResponses>, t: Throwable) {
                    Toast.makeText(requireActivity(), "Error fetching data", Toast.LENGTH_SHORT).show()
                    progressBar.visibility=View.GONE
                }
            })
    }



    @RequiresApi(Build.VERSION_CODES.Q)
    private fun exportToPdf() {
         progressBar.visibility = View.VISIBLE
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

        progressBar.visibility = View.VISIBLE
        val resolver = requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "supply_report.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri == null) {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Error creating file", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
            return
        }

        try {
            val outputStream = resolver.openOutputStream(uri)
            val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(com.itextpdf.kernel.pdf.PdfWriter(outputStream))
            val document = com.itextpdf.layout.Document(pdfDocument)

            val font = PdfFontFactory.createFont(StandardFonts.HELVETICA)
            val title = Paragraph("Supply Report")
                .setFont(font)
                .setFontSize(20f)
                .setTextAlignment(TextAlignment.CENTER)
            document.add(title)
            document.add(Paragraph("\n").setFont(font))

            val adapter = recyclerView.adapter as SupplyReportAdapter
            val summaryList = adapter.getSupplyReportList()

            // Define a table with the number of columns matching the fields
            val table = com.itextpdf.layout.element.Table(floatArrayOf(1f, 2f, 2f, 2f)).apply {
                setWidth(com.itextpdf.layout.property.UnitValue.createPercentValue(100f))
            }

            // Add table headers
            table.addHeaderCell(Cell().add(Paragraph("SE Name").setFont(font)))
            table.addHeaderCell(Cell().add(Paragraph("BP Code").setFont(font)))
            table.addHeaderCell(Cell().add(Paragraph("Date").setFont(font)))
            table.addHeaderCell(Cell().add(Paragraph("Sum of PV Total").setFont(font)))

            // Add the summary data to the table
            summaryList.forEach { summary ->
                table.addCell(Cell().add(Paragraph(summary.SEname).setFont(font)))
                table.addCell(Cell().add(Paragraph(summary.BPcode.toString()).setFont(font)))
                table.addCell(Cell().add(Paragraph(summary.Date).setFont(font)))
                table.addCell(Cell().add(Paragraph(summary.SumofPv).setFont(font)))
            }

            document.add(table) // Add the table to the document

            document.close()
            outputStream?.close()

            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "PDF generated successfully", Toast.LENGTH_SHORT).show()
                showNotification(uri)
                progressBar.visibility = View.GONE

            }

        } catch (e: Exception) {
            e.printStackTrace()
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Error generating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
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
