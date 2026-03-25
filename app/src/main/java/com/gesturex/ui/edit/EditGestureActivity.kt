package com.gesturex.ui.edit

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gesturex.R
import com.gesturex.data.model.AcaoGesto
import com.gesturex.data.model.Gesture
import com.gesturex.data.model.TipoGesto
import com.gesturex.databinding.ActivityEditGestureBinding
import com.gesturex.service.GestureService
import com.gesturex.ui.home.GestureViewModel
import kotlinx.coroutines.launch

class EditGestureActivity : AppCompatActivity() {
    private lateinit var b: ActivityEditGestureBinding
    private lateinit var vm: GestureViewModel

    private var tipoGesto = TipoGesto.PERSONALIZADO
    private var dadosSensor = ""
    private var gestureId = 0
    private var acaoSelecionada = AcaoGesto.ABRIR_APP
    private var appPkg = ""; private var appNome = ""
    private var contatoNum = ""; private var contatoNome = ""

    private val pickApp = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { r ->
        if (r.resultCode == Activity.RESULT_OK) {
            appPkg  = r.data?.getStringExtra("pkg") ?: ""; appNome = r.data?.getStringExtra("nome") ?: ""
            b.textAppSelecionado.text = if (appNome.isNotBlank()) "App: $appNome" else ""
            b.textAppSelecionado.visibility = if (appNome.isNotBlank()) View.VISIBLE else View.GONE
        }
    }

    private val pickContact = registerForActivityResult(ActivityResultContracts.PickContact()) { uri ->
        uri ?: return@registerForActivityResult
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                contatoNome = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)) ?: ""
                val id = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?", arrayOf(id), null)
                phones?.use { p -> if (p.moveToFirst()) contatoNum = p.getString(p.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)) ?: "" }
            }
        }
        b.textContatoSelecionado.text = if (contatoNome.isNotBlank()) "Contato: $contatoNome" else ""
        b.textContatoSelecionado.visibility = if (contatoNome.isNotBlank()) View.VISIBLE else View.GONE
    }

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityEditGestureBinding.inflate(layoutInflater); setContentView(b.root)
        vm = ViewModelProvider(this)[GestureViewModel::class.java]

        tipoGesto  = intent.getStringExtra("tipo_gesto") ?: TipoGesto.PERSONALIZADO
        dadosSensor = intent.getStringExtra("dados_sensor") ?: ""
        gestureId  = intent.getIntExtra("gesture_id", 0)

        b.textTipoGesto.text = TipoGesto.label(tipoGesto)
        b.btnVoltar.setOnClickListener { finish() }

        if (gestureId != 0) {
            lifecycleScope.launch {
                val g = vm.getById(gestureId) ?: return@launch
                b.editNome.setText(g.nome)
                tipoGesto = g.tipoGesto; dadosSensor = g.dadosSensor
                acaoSelecionada = g.acaoDestino
                appPkg = g.appDestino; appNome = g.appNome
                contatoNum = g.contatoNumero; contatoNome = g.contatoNome
                b.textTipoGesto.text = TipoGesto.label(tipoGesto)
                atualizarSelecaoAcao()
            }
        }

        setupAcoes()
        b.btnSalvar.setOnClickListener { salvar() }
        b.btnDeletar.visibility = if (gestureId != 0) View.VISIBLE else View.GONE
        b.btnDeletar.setOnClickListener { deletar() }
    }

    private fun setupAcoes() {
        b.cardAcaoApp.setOnClickListener      { acaoSelecionada = AcaoGesto.ABRIR_APP;  atualizarSelecaoAcao() }
        b.cardAcaoLanterna.setOnClickListener { acaoSelecionada = AcaoGesto.LANTERNA;   atualizarSelecaoAcao() }
        b.cardAcaoSilenciar.setOnClickListener{ acaoSelecionada = AcaoGesto.SILENCIAR;  atualizarSelecaoAcao() }
        b.cardAcaoLigar.setOnClickListener    { acaoSelecionada = AcaoGesto.LIGAR_PARA; atualizarSelecaoAcao() }
        b.btnEscolherApp.setOnClickListener   { pickApp.launch(Intent(this, AppPickerActivity::class.java)) }
        b.btnEscolherContato.setOnClickListener { pickContact.launch(Uri.EMPTY) }
        atualizarSelecaoAcao()
    }

    private fun atualizarSelecaoAcao() {
        val sel = com.google.android.material.color.MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary, 0)
        listOf(b.cardAcaoApp, b.cardAcaoLanterna, b.cardAcaoSilenciar, b.cardAcaoLigar).forEach {
            it.strokeWidth = 0; it.strokeColor = android.content.res.ColorStateList.valueOf(0)
        }
        val card = when (acaoSelecionada) {
            AcaoGesto.ABRIR_APP  -> b.cardAcaoApp
            AcaoGesto.LANTERNA   -> b.cardAcaoLanterna
            AcaoGesto.SILENCIAR  -> b.cardAcaoSilenciar
            AcaoGesto.LIGAR_PARA -> b.cardAcaoLigar
            else -> b.cardAcaoApp
        }
        card.strokeWidth = 4
        card.strokeColor = android.content.res.ColorStateList.valueOf(
            resources.getColor(R.color.primary, theme))
        b.layoutEscolherApp.visibility     = if (acaoSelecionada == AcaoGesto.ABRIR_APP)  View.VISIBLE else View.GONE
        b.layoutEscolherContato.visibility = if (acaoSelecionada == AcaoGesto.LIGAR_PARA) View.VISIBLE else View.GONE
        if (appNome.isNotBlank()) { b.textAppSelecionado.text = "App: $appNome"; b.textAppSelecionado.visibility = View.VISIBLE }
        if (contatoNome.isNotBlank()) { b.textContatoSelecionado.text = "Contato: $contatoNome"; b.textContatoSelecionado.visibility = View.VISIBLE }
    }

    private fun salvar() {
        val nome = b.editNome.text.toString().trim()
        if (nome.isBlank()) { b.editNome.error = "Digite um nome"; return }
        if (acaoSelecionada == AcaoGesto.ABRIR_APP && appPkg.isBlank()) { Toast.makeText(this,"Escolha um app",Toast.LENGTH_SHORT).show(); return }
        if (acaoSelecionada == AcaoGesto.LIGAR_PARA && contatoNum.isBlank()) { Toast.makeText(this,"Escolha um contato",Toast.LENGTH_SHORT).show(); return }
        val g = Gesture(id=gestureId, nome=nome, tipoGesto=tipoGesto, acaoDestino=acaoSelecionada,
            appDestino=appPkg, appNome=appNome, contatoNumero=contatoNum, contatoNome=contatoNome,
            ativo=true, dadosSensor=dadosSensor)
        vm.salvar(g)
        GestureService.start(this)
        Toast.makeText(this, getString(R.string.gesto_salvo), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun deletar() {
        lifecycleScope.launch {
            val g = vm.getById(gestureId) ?: return@launch
            vm.deletar(g)
            finish()
        }
    }
}
