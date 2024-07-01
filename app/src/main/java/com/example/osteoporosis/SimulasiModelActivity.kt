package com.example.osteoporosis

import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.osteoporosis.databinding.ActivitySimulasiModelBinding
import com.google.android.material.textfield.TextInputEditText
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulasiModelActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySimulasiModelBinding
    private lateinit var interpreter: Interpreter
    private val mModelPath = "osteoporosis.tflite"


    private lateinit var itAge: TextInputEditText
    private lateinit var itGender: AutoCompleteTextView
    private lateinit var itHormonalChanges: AutoCompleteTextView
    private lateinit var itFamilyHistory: AutoCompleteTextView
    private lateinit var itEthnic: AutoCompleteTextView
    private lateinit var itWeight: AutoCompleteTextView
    private lateinit var itCalcium: AutoCompleteTextView
    private lateinit var itVitD: AutoCompleteTextView
    private lateinit var itPhysical: AutoCompleteTextView
    private lateinit var itSmoking: AutoCompleteTextView
    private lateinit var itMedical: AutoCompleteTextView
    private lateinit var checkButton : Button
    private lateinit var itFractures: AutoCompleteTextView
    private lateinit var resultText: TextView
    var data = InputData(0,0,0,0,0,0,0,0,0,0,0,0)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySimulasiModelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setOnClickListener{
            onBackPressed()
        }
        initInterpreter()
        setupListener()

    }
    private fun setupListener(){
        val jenisKelamin = resources.getStringArray(R.array.jenis_kelamin)
        val jkAdapter = ArrayAdapter(this,R.layout.dropdown,jenisKelamin)
        val hormonAdapter = ArrayAdapter(this,R.layout.dropdown, resources.getStringArray(R.array.hormon))

        itGender = findViewById(R.id.tiGender)
        itGender.setAdapter(jkAdapter)

        itHormonalChanges = findViewById(R.id.tiHormonalChanges)
        itHormonalChanges.setAdapter(hormonAdapter)
        itHormonalChanges.setOnItemClickListener { parent, view, position, id ->
            data.hormone = position


        }
        itEthnic = findViewById(R.id.tiEthnic)
        itEthnic.setAdapter(ArrayAdapter(this,R.layout.dropdown, resources.getStringArray(R.array.Etnis)))
        itEthnic.setOnItemClickListener { parent, view, position, id ->
            data.ethnic = position

        }
        itFamilyHistory = findViewById(R.id.tiFamilyHistory)
        itFamilyHistory.setAdapter(ArrayAdapter(this,R.layout.dropdown, resources.getStringArray(R.array.Riwayat)))
        itFamilyHistory.setOnItemClickListener { parent, view, position, id ->
            data.history = position

        }
        itWeight = findViewById(R.id.tiWeight)
        itWeight.setAdapter(ArrayAdapter(this,R.layout.dropdown, resources.getStringArray(R.array.Berat)))
        itWeight.setOnItemClickListener { parent, view, position, id ->
            data.weight = position

        }
        itCalcium = findViewById(R.id.tiCalcium)
        itCalcium.setAdapter(ArrayAdapter(this,R.layout.dropdown, resources.getStringArray(R.array.Kalsium)))
        itCalcium.setOnItemClickListener { parent, view, position, id ->
            data.calcium = position

        }
        itVitD = findViewById(R.id.tiVitD)
        itVitD.setAdapter(ArrayAdapter(this,R.layout.dropdown, resources.getStringArray(R.array.VitD)))
        itVitD.setOnItemClickListener { parent, view, position, id ->
            data.vit = position

        }
        itPhysical = findViewById(R.id.tiPhysical)
        itPhysical.setAdapter(ArrayAdapter(this,R.layout.dropdown, resources.getStringArray(R.array.Aktivitas)))
        itPhysical.setOnItemClickListener { parent, view, position, id ->
            data.physical = position

        }
        itSmoking = findViewById(R.id.tiSmoking)
        itSmoking.setAdapter(ArrayAdapter(this,R.layout.dropdown, resources.getStringArray(R.array.Merokok)))
        itSmoking.setOnItemClickListener { parent, view, position, id ->
            data.smoking = position

        }
        itMedical = findViewById(R.id.tiMedical)
        itMedical.setAdapter(ArrayAdapter(this,R.layout.dropdown, resources.getStringArray(R.array.Kondisi)))
        itMedical.setOnItemClickListener { parent, view, position, id ->
            data.medical = position

        }
        itFractures = findViewById(R.id.tiFractures)
        itFractures.setAdapter(ArrayAdapter(this,R.layout.dropdown, resources.getStringArray(R.array.Patah)))
        itFractures.setOnItemClickListener { parent, view, position, id ->
            data.fractures = position

        }

        checkButton = findViewById(R.id.btnCheck)
        resultText = findViewById(R.id.txtResult)

        checkButton.setOnClickListener {
            data.age = binding.tiAge.text.toString().toInt()
//            Log.d("InputData", data.hormone.toString())// Tambahkan log ini
            var result = doInference(
                data)
            runOnUiThread {
                if (result == 0) {
                    dialog("Tidak Terkena Osteoporosis")
                }else if (result == 1){
                    dialog("Terkena Penyakit Osteoporosis")
                }
            }
        }
    }

    private fun dialog(predict:String) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog, null)

        val builder = AlertDialog.Builder(this)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        val tvPredict = view.findViewById<TextView>(R.id.tvPredict)

        // Mengubah teks pada TextView
        tvPredict.text = predict // Ganti teks sesuai kebutuhan

        // Mengakses tombol di dalam dialog menggunakan findViewById
        val btnConfirm: Button = view.findViewById(R.id.btn_confirm)
        btnConfirm.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun initInterpreter() {
        val options = Interpreter.Options()
        options.setNumThreads(8) // Mengatur jumlah thread untuk menjalankan inference
        options.setUseNNAPI(true) // Menggunakan NNAPI jika tersedia

        // Memuat model TensorFlow Lite
        interpreter = Interpreter(loadModelFile(assets, mModelPath), options)
    }

    // Melakukan inference dengan input data
    private fun doInference(dataInput: InputData): Int {
        Log.d("Inputdaata", "${dataInput.toString()}")
        val inputVal = floatArrayOf(
            dataInput.age.toFloat(),
            dataInput.gender.toFloat(),
            dataInput.hormone.toFloat(),
            dataInput.history.toFloat(),
            dataInput.ethnic.toFloat(),
            dataInput.weight.toFloat(),
            dataInput.calcium.toFloat(),
            dataInput.vit.toFloat(),
            dataInput.physical.toFloat(),
            dataInput.smoking.toFloat(),
            dataInput.medical.toFloat(),
            dataInput.fractures.toFloat()
        )

        val output = Array(1) { FloatArray(2) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())
        Log.d("asdf", "doInference: ${inputVal[11]}")
        Log.d("asdf", "doInference: ${dataInput.age}")

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    // Memuat file model TensorFlow Lite dari assets
    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}