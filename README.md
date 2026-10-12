# Call Shield - Gerenciador Avançado de Chamadas para Android

Aplicativo utilitário Android projetado com arquitetura moderna (**Jetpack**, **Material 3**, **CallScreeningService** e **Foreground Services**) para bloqueio inteligente de chamadas a nível de chip (SIM) e gravação de áudio com persistência estruturada.

---

## 🎯 Funcionalidades Principais

1. **Bloqueio de Chamadas de Rede Celular (SIM/Carrier)**:
   - Utiliza a API nativa do Android 10+ (`android.telecom.CallScreeningService`).
   - Intercepta e rejeita chamadas convencionais da operadora sem tocar o telefone.
   - **Whitelisting VoIP Nativo**: Chamadas de voz e vídeo via dados de internet (WhatsApp, Telegram, etc.) permanecem 100% operacionais e autorizadas.

2. **Módulo de Gravação em Primeiro Plano**:
   - Executa via `ForegroundService` com notificação visível do sistema (`foregroundServiceType="microphone"`).
   - Suporte aos formatos de codificação de áudio configuráveis.
   - Armazenamento com padrão de nomenclatura: `ANO-MES-DIA_HORA_NumeroDoTelefone.extensao`.

3. **Interface e UX Descomplicada**:
   - Interruptores rápidos (*MaterialSwitch*) para controle independente dos módulos.
   - Solicitação didática de permissões críticas de tempo de execução (`RECORD_AUDIO`, `ROLE_CALL_SCREENING`).
   - Botão integrado de desinstalação limpa (`ACTION_UNINSTALL_PACKAGE`), restaurando imediatamente o comportamento padrão do sistema.

4. **Ícone Inicializável Integrado**:
   - Recurso oficial de *Adaptive Icon* vetorial (`ic_launcher.xml` e `ic_launcher_round.xml`).
   - Arte gráfica de alta resolução incluída (`app_icon.png`).

---

## 🏗️ Estrutura do Projeto

```text
CallShieldApp/
├── .github/
│   └── workflows/
│       └── build_apk.yml             # Pipeline de compilação automática na nuvem (GitHub Actions)
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml       # Permissões e declaração dos serviços
│   │   ├── java/com/exemplo/callutility/
│   │   │   ├── service/
│   │   │   │   ├── SimCallScreeningService.kt   # Interceptação e bloqueio de linha celular
│   │   │   │   └── CallRecorderService.kt       # Gravador de áudio em primeiro plano
│   │   │   └── ui/
│   │   │       └── MainActivity.kt              # Tela inicial, controles e desinstalação
│   │   └── res/
│   │       ├── drawable/                        # Vetores do ícone adaptativo
│   │       ├── layout/activity_main.xml         # Layout moderno Material 3
│   │       ├── mipmap-anydpi-v26/               # Definições do launcher icon
│   │       └── values/                          # Temas e textos
│   └── build.gradle
├── gradle/wrapper/                   # Gradle Wrapper 8.5
├── build.gradle                      # Configuração raiz
├── gradle.properties                 # Configurações do AndroidX e JVM
├── settings.gradle
└── app_icon.png                      # Imagem do ícone em alta resolução
```

---

## 🚀 Como Compilar e Baixar o APK

### Opção 1: Via GitHub Actions (Sem instalar nada no PC)
- Repositório: [https://github.com/lucianogaleski-maker/CallShield](https://github.com/lucianogaleski-maker/CallShield)
- Acesse a aba **Actions** ➔ selecione a execução concluída ➔ na seção **Artifacts**, baixe o pacote **`CallShield-app-debug`**.
- O arquivo `.apk` compilado e assinado em modo debug estará pronto para instalação.

### Opção 2: Localmente via Android Studio
1. Abra o Android Studio.
2. Abra a pasta `C:\Users\lucia\.gemini\antigravity-ide\scratch\CallShieldApp`.
3. Vá em **Build** ➔ **Build Bundle(s) / APK(s)** ➔ **Build APK(s)**.
