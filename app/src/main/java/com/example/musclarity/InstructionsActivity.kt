package com.example.musclarity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.core.content.ContextCompat

class InstructionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_instructions)

        val backButton: ImageView = findViewById(R.id.back_button)

        // Move to squad activity on click
        backButton.setOnClickListener {
            val intent1 = Intent(this, GraphActivity::class.java)
            startActivity(intent1)
        }

        val textViewI = findViewById<TextView>(R.id.I)
        val iString = SpannableString("I. Preparación: Acostate boca abajo con las piernas extendidas y los pies relajados. Asegurate de estar cómodo.")
        val iStart = 0
        val iEnd = 15
        // Apply bold style
        iString.setSpan(
            StyleSpan(Typeface.BOLD),
            iStart,
            iEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // Apply color
        iString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.logoColor)),
            iStart,
            iEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textViewI.text = iString

        val textViewII = findViewById<TextView>(R.id.II)
        val iiString = SpannableString("II. Preparación de la piel: Limpiá y secá cuidadosamente la piel alrededor de la parte posterior del muslo, donde vas a colocar los electrodos. Esto te va a garantizar una buena adherencia y una lectura precisa. ¡Es vital que no te saltees este paso!")
        val iiStart = 0
        val iiEnd = 27
        // Apply bold style
        iiString.setSpan(
            StyleSpan(Typeface.BOLD),
            iiStart,
            iiEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // Apply color
        iiString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.logoColor)),
            iiStart,
            iiEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textViewII.text = iiString

        val textViewIII = findViewById<TextView>(R.id.III)
        val iiiString = SpannableString("III. Determinación de la ubicación: Localizá el punto medio entre el hueso del glúteo (tuberosidad isquiática) y el lado externo de la rodilla (cóndilo tibial lateral), como se muestra en la imagen debajo. A cada lado del punto marcado con una cruz vas a colocar los electrodos de color verde y rojo, manteniendo una distancia de aproximadamente 20mm entre ellos. Posicionalos de forma tal que los electrodos estén alineados en la dirección de la línea que une el hueso del glúteo con el lado externo de la rodilla.")
        val iiiStart = 0
        val iiiEnd = 35
        // Apply bold style
        iiiString.setSpan(
            StyleSpan(Typeface.BOLD),
            iiiStart,
            iiiEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // Apply color
        iiiString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.logoColor)),
            iiiStart,
            iiiEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textViewIII.text = iiiString

        val textViewIV = findViewById<TextView>(R.id.IV)
        val ivString = SpannableString("IV. Colocación del electrodo de referencia: Colocá el electrodo amarillo (de referencia) en el hueso que sobresale al costado de la cintura (cresta ilíaca), del lado de la pierna donde se colocaron los otros electrodos. Esto va a ayudar a minimizar las interferencias externas y obtener lecturas más precisas.")
        val ivStart = 0
        val ivEnd = 43
        // Apply bold style
        ivString.setSpan(
            StyleSpan(Typeface.BOLD),
            ivStart,
            ivEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // Apply color
        ivString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.logoColor)),
            ivStart,
            ivEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textViewIV.text = ivString

        val textViewV = findViewById<TextView>(R.id.V)
        val vString = "V. Fijación de los electrodos: En caso de no tener una calza deportiva que permita fijar los electrodos en su lugar, es recomendable usar cinta adhesiva de doble cara para asegurarlos. Corroborá que los electrodos estén bien fijados a la piel para evitar movimientos que puedan afectar la lectura."

        // Aplicar formato
        val formattedTextV = SpannableString(vString)
        formattedTextV.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            vString.indexOf(":") + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        formattedTextV.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.logoColor)),
            0,
            vString.indexOf(":") + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textViewV.text = formattedTextV

        val textViewVI = findViewById<TextView>(R.id.VI)
        val viString = "VI. Verificación y ajustes: Antes de empezar con el registro de datos, corroborá que todos los electrodos estén firmemente fijados y se sientan cómodos. Desde Musclarity, recomendamos fuertemente la visualización de la señal obtenida como método de verificación:"

        // Aplicar formato
        val formattedTextVI = SpannableString(viString)
        formattedTextVI.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            viString.indexOf(":") + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        formattedTextVI.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.logoColor)),
            0,
            viString.indexOf(":") + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textViewVI.text = formattedTextVI

        val textViewVI1 = findViewById<TextView>(R.id.VI_1)
        val vi1String = "1) Una vez que sientas que los electrodos están bien puestos, dirigite a la visión gráfica."
        // Aplicar formato
        val formattedTextVI1 = SpannableString(vi1String)
        formattedTextVI1.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        formattedTextVI1.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.numColor)),
            0,
            2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        formattedTextVI1.setSpan(
            StyleSpan(Typeface.BOLD),
            76,
            90,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        formattedTextVI1.setSpan(
            UnderlineSpan(),
            76,
            90,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textViewVI1.text = formattedTextVI1

        val textViewVI2 = findViewById<TextView>(R.id.VI_2)
        val vi2String = "2) Conectá tu dispositivo móvil al detector de fatiga (nombre: Musclarity; contraseña: 1234), y presioná el botón Turn On."
        // Aplicar formato
        val formattedTextVI2 = SpannableString(vi2String)
        formattedTextVI2.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        formattedTextVI2.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.numColor)),
            0,
            2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        formattedTextVI2.setSpan(
            StyleSpan(Typeface.BOLD),
            114,
            121,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        formattedTextVI2.setSpan(
            UnderlineSpan(),
            114,
            121,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textViewVI2.text = formattedTextVI2

        val textViewVI3 = findViewById<TextView>(R.id.VI_3)
        val vi3String = "3) Esperá unos segundos hasta lograr ver un marco completo en el gráfico (aproximadamente 10 segundos). Compará los resultados obtenidos con las imágenes provistas debajo. Si los electrodos están correctamente posicionados, la gráfica debería verse similar a la figura titulada “NORMAL”."
        // Aplicar formato
        val formattedTextVI3 = SpannableString(vi3String)
        formattedTextVI3.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        formattedTextVI3.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.numColor)),
            0,
            2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        formattedTextVI3.setSpan(
            StyleSpan(Typeface.BOLD),
            278,
            286,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        formattedTextVI3.setSpan(
            UnderlineSpan(),
            278,
            286,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textViewVI3.text = formattedTextVI3

        val textViewVI4 = findViewById<TextView>(R.id.VI_4)
        val vi4String = "4) En caso de observar algo similar a la figura titulada “DEFECTUOSA”, verifica que los electrodos estén bien adheridos y en su posición. Si el problema persiste, intenta reubicar el electrodo de referencia de manera que esté lo más cercano posible al hueso. Recuerda que cuanto más puro sea el contacto entre este electrodo y el hueso de la cintura, mejor cumplirá su función."
        // Aplicar formato
        val formattedTextVI4 = SpannableString(vi4String)
        formattedTextVI4.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        formattedTextVI4.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.numColor)),
            0,
            2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        formattedTextVI4.setSpan(
            StyleSpan(Typeface.BOLD),
            57,
            69,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        formattedTextVI4.setSpan(
            UnderlineSpan(),
            57,
            69,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textViewVI4.text = formattedTextVI4

    }
}