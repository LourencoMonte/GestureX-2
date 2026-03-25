# GestureX 📱🤚

**Aplicativo Android de controle por gestos usando sensores nativos do dispositivo.**

GestureX permite ao usuário criar gestos personalizados (usando acelerômetro e giroscópio) e associá-los a ações no celular — como abrir apps, ligar/desligar a lanterna, silenciar o telefone ou ligar para contatos. A detecção funciona em segundo plano via Foreground Service.

---

## 📋 Problema Identificado

Pessoas com mobilidade reduzida ou em situações onde não podem interagir com a tela (dirigindo, cozinhando, com as mãos ocupadas) têm dificuldade em acessar funções rápidas do celular. O GestureX resolve isso ao permitir o controle do dispositivo por meio de movimentos físicos simples, sem tocar na tela.

---

## 🎯 Funcionalidades (MVP)

| Funcionalidade | Descrição |
|---|---|
| **Gravar gesto** | Grava dados do acelerômetro por 2,5s e classifica o gesto automaticamente |
| **Editar gesto** | Nomear gesto, escolher ação (abrir app, lanterna, silenciar, ligar para contato) |
| **Detecção em background** | Foreground Service monitora gestos continuamente |
| **Fusão de sensores** | Acelerômetro + Giroscópio para detecção mais precisa |
| **Configurações** | Ativar/desativar serviço, ajustar sensibilidade (alta/média/baixa) |
| **Notificação persistente** | Indica que o serviço está ativo |

---

## 🏗️ Arquitetura

```
com.gesturex/
├── data/
│   ├── db/              # Room Database (GestureDatabase, GestureDao)
│   ├── model/           # Entidade Gesture, constantes TipoGesto e AcaoGesto
│   └── repository/      # GestureRepository (camada de abstração)
├── service/
│   └── GestureService   # Foreground Service com SensorEventListener
├── ui/
│   ├── main/            # MainActivity (Navigation + BottomNav)
│   ├── home/            # HomeFragment, GestureAdapter, GestureViewModel
│   ├── record/          # RecordGestureActivity (captura do sensor)
│   ├── edit/            # EditGestureActivity, AppPickerActivity
│   └── settings/        # SettingsFragment
└── util/
    ├── GestureDetectorUtil  # Algoritmo de detecção com fusão acelerômetro+giroscópio
    ├── GestureRecorder      # Gravação e classificação de gestos
    └── ActionDispatcher     # Execução das ações (app, lanterna, silenciar, ligar)
```

**Padrão:** MVVM (ViewModel + LiveData + Repository + Room)

---

## 📱 Sensores Nativos Utilizados

| Sensor | Uso |
|---|---|
| **Acelerômetro** (`TYPE_ACCELEROMETER`) | Sensor principal para detecção de gestos (agitar, inclinar, girar, virar) |
| **Giroscópio** (`TYPE_GYROSCOPE`) | Fusão com acelerômetro para confirmar rotações reais e reduzir falsos positivos |
| **Câmera** (via `CameraManager`) | Controle da lanterna (flash) como ação de gesto |

---

## 🔐 Permissões

| Permissão | Motivo |
|---|---|
| `CAMERA` | Controlar lanterna (flash LED) |
| `CALL_PHONE` | Realizar ligações via gesto |
| `READ_CONTACTS` | Selecionar contato para ação "ligar para" |
| `FOREGROUND_SERVICE` | Manter detecção de gestos em segundo plano |
| `RECEIVE_BOOT_COMPLETED` | Reiniciar serviço após boot |

Permissões perigosas são solicitadas em **runtime** conforme exigido pelo Android 6+.

---

## 🎨 Material Design

- Tema: `Theme.MaterialComponents.DayNight.NoActionBar` (dark theme)
- Componentes: `MaterialCardView`, `MaterialButton`, `ExtendedFloatingActionButton`, `BottomNavigationView`, `TextInputLayout`, `SwitchMaterial`
- Navigation Component com `NavHostFragment` + `BottomNavigationView`
- Paleta de cores consistente com contraste adequado

---

## 🗄️ Armazenamento Local

- **Room Database** (`gesturex_db`): Persistência dos gestos criados pelo usuário
- **SharedPreferences** (`gesturex_prefs`): Configurações (sensibilidade do sensor, estado do serviço)

---

## 🧪 Testes

- **Testes unitários** (`app/src/test/`):
  - `GestureDetectorUtilTest` — 13 testes cobrindo detecção de cada tipo de gesto, cooldown, sensibilidade e fusão de sensores
  - `GestureRecorderTest` — 12 testes cobrindo gravação, classificação e limiares
  - `GestureModelTest` — 6 testes cobrindo modelo de dados, labels e constantes
- **Testes instrumentados** (`app/src/androidTest/`): Teste de contexto básico

### Executar testes:
```bash
./gradlew test
```

---

## 🛠️ Tecnologias

| Tecnologia | Versão |
|---|---|
| Kotlin | 1.9.x |
| Android SDK | compileSdk 34, minSdk 26 |
| Room | 2.6.1 |
| Navigation | 2.7.7 |
| Material Components | 1.11.0 |
| Lifecycle (ViewModel/LiveData) | 2.7.0 |
| Coroutines | 1.7.3 |
| Gson | 2.10.1 |

---

## 🚀 Como compilar e executar

1. Clone o repositório:
   ```bash
   git clone https://github.com/<seu-usuario>/GestureX-2.git
   ```
2. Abra no Android Studio (Hedgehog ou superior)
3. Sincronize o Gradle
4. Conecte um dispositivo Android físico (sensores não funcionam em emulador)
5. Execute o app (`Run > Run 'app'`)

---

## 👥 Equipe

Projeto desenvolvido para a disciplina **N700 — Plataformas Móveis**.

---

## 📄 Licença

Projeto acadêmico — uso educacional.

