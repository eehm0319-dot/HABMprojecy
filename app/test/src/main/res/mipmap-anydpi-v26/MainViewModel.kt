package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiApiManager
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(
    application: Application,
    private val repository: SocialRepository
) : AndroidViewModel(application) {

    // Reactive database flows
    private val _posts = MutableStateFlow<List<SocialPost>>(emptyList())
    val posts: StateFlow<List<SocialPost>> = _posts.asStateFlow()

    private val _profiles = MutableStateFlow<List<SocialProfile>>(emptyList())
    val profiles: StateFlow<List<SocialProfile>> = _profiles.asStateFlow()

    private val _assets = MutableStateFlow<List<AssetItem>>(emptyList())
    val assets: StateFlow<List<AssetItem>> = _assets.asStateFlow()

    // View States
    var selectedTab by mutableStateOf(0) // 0=Dashboard, 1=Calendar, 2=Creator, 3=Settings
    var editingPost by mutableStateOf<SocialPost?>(null)
    var isGeneratingState by mutableStateOf(false)
    var aiGeneratorResponse by mutableStateOf("")

    // HABM persistent preferences
    private val prefs = application.getSharedPreferences("habm_prefs", android.content.Context.MODE_PRIVATE)
    
    var activeNiche by mutableStateOf(prefs.getString("active_niche", "Finanzas y Política") ?: "Finanzas y Política")
        private set

    fun updateActiveNiche(newNiche: String) {
        activeNiche = newNiche
        prefs.edit().putString("active_niche", newNiche).apply()
    }

    var isFacebookLinked by mutableStateOf(prefs.getBoolean("link_fb", true))
        private set
    var isInstagramLinked by mutableStateOf(prefs.getBoolean("link_ig", true))
        private set
    var isTikTokLinked by mutableStateOf(prefs.getBoolean("link_tiktok", false))
        private set
    var isYouTubeLinked by mutableStateOf(prefs.getBoolean("link_yt", true))
        private set
    var isWhatsAppLinked by mutableStateOf(prefs.getBoolean("link_wa", false))
        private set
    var isGoogleAccountLinked by mutableStateOf(prefs.getBoolean("link_google", true))
        private set

    fun toggleLinkStatus(account: String) {
        when (account) {
            "fb" -> {
                isFacebookLinked = !isFacebookLinked
                prefs.edit().putBoolean("link_fb", isFacebookLinked).apply()
            }
            "ig" -> {
                isInstagramLinked = !isInstagramLinked
                prefs.edit().putBoolean("link_ig", isInstagramLinked).apply()
            }
            "tiktok" -> {
                isTikTokLinked = !isTikTokLinked
                prefs.edit().putBoolean("link_tiktok", isTikTokLinked).apply()
            }
            "yt" -> {
                isYouTubeLinked = !isYouTubeLinked
                prefs.edit().putBoolean("link_yt", isYouTubeLinked).apply()
            }
            "wa" -> {
                isWhatsAppLinked = !isWhatsAppLinked
                prefs.edit().putBoolean("link_wa", isWhatsAppLinked).apply()
            }
            "google" -> {
                isGoogleAccountLinked = !isGoogleAccountLinked
                prefs.edit().putBoolean("link_google", isGoogleAccountLinked).apply()
            }
        }
    }

    // Active filters
    var assetSearchQuery by mutableStateOf("")
    var assetTypeFilter by mutableStateOf("ALL") // "ALL", "IMAGE", "VIDEO", "TEXT_TEMPLATE"

    private val geminiManager = GeminiApiManager()

    init {
        // Run database seeding and begin observing
        viewModelScope.launch {
            repository.checkAndSeedDatabase()
            
            // Read streams
            launch {
                repository.allPosts.collectLatest {
                    _posts.value = it
                }
            }
            launch {
                repository.allProfiles.collectLatest {
                    _profiles.value = it
                }
            }
            launch {
                repository.allAssets.collectLatest {
                    _assets.value = it
                }
            }
        }
    }

    // --- Post Actions ---
    fun savePost(post: SocialPost) {
        viewModelScope.launch {
            if (post.id == 0) {
                repository.insertPost(post)
            } else {
                repository.updatePost(post)
            }
        }
    }

    fun deletePost(post: SocialPost) {
        viewModelScope.launch {
            repository.deletePost(post)
            if (editingPost?.id == post.id) {
                editingPost = null
            }
        }
    }

    fun publishNow(post: SocialPost) {
        viewModelScope.launch {
            // Generate some realistic engagement stats upon mock publication
            val rand = java.util.Random()
            val updated = post.copy(
                status = "PUBLISHED",
                clicks = rand.nextInt(300) + 50,
                likes = rand.nextInt(500) + 100,
                comments = rand.nextInt(80) + 10,
                shares = rand.nextInt(40) + 5
            )
            repository.updatePost(updated)
            if (editingPost?.id == post.id) {
                editingPost = updated
            }
        }
    }

    fun optimizePostTime(post: SocialPost): SocialPost {
        // Shift scheduled time to high-performance timeslot (e.g. 18:00 / 6:00 PM of scheduled day)
        val cal = Calendar.getInstance()
        cal.timeInMillis = post.scheduledTime
        cal.set(Calendar.HOUR_OF_DAY, 18)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return post.copy(scheduledTime = cal.timeInMillis)
    }

    // --- Profile Actions ---
    fun toggleProfileConnection(profile: SocialProfile) {
        viewModelScope.launch {
            repository.updateProfile(profile.copy(isConnected = !profile.isConnected))
        }
    }

    // --- Asset Actions ---
    fun saveAsset(name: String, path: String, tags: String, type: String) {
        viewModelScope.launch {
            val asset = AssetItem(
                name = name,
                mediaPath = path,
                mimeType = if (type == "TEXT_TEMPLATE") "text/plain" else "image/png",
                tags = tags,
                type = type
            )
            repository.insertAsset(asset)
        }
    }

    fun deleteAsset(asset: AssetItem) {
        viewModelScope.launch {
            repository.deleteAsset(asset)
        }
    }

    // --- AI Generator Actions ---
    // --- AI Generator Actions ---
    var isNicheGenerating by mutableStateOf(false)
    var nicheGeneratedResult by mutableStateOf<NicheContentResult?>(null)
    var nicheError by mutableStateOf<String?>(null)

    fun generateNicheContentWithAI(niche: String) {
        viewModelScope.launch {
            isNicheGenerating = true
            nicheError = null
            nicheGeneratedResult = null

            val systemPrompt = """
                Eres un estratega experto de marketing digital de SocialGrow. El usuario te proporcionará un nicho específico, por ejemplo: "Finanzas Personales" o "Fitness y Alimentación".
                Debes generar una propuesta integral de contenidos estructurada bajo rigurosos tags de sección.
                
                A continuación se listan las especificaciones por plataforma que DEBES seguir estrictamente:
                
                1. FACEBOOK (Frecuencia: Cada 4 horas):
                   - [FB_HOOK]: Genera una frase gancho de exactamente 15 palabras de longitud (ni una más, ni una menos) encerrada entre comillas "". Debe ser súper impactante y relevante al nicho.
                   - [FB_IMAGE_DESC]: Describe detalladamente una propuesta visual de imagen moderna y atractiva donde se insertaría la frase gancho anterior (ej: "un fondo oscuro minimalista con barras financieras...").
                   - [FB_CAPTION]: Escribe un copy/descripción envolvente de retención extrema para el espectador. Debe terminar con una excelente frase aleatoria de incertidumbre profunda o motivación inspiradora. Al final, incluye exactamente 4 hashtags del tema.
                   
                2. REELS para Facebook, Instagram y TikTok (Frecuencia: 1 diario):
                   - [REELS_SCRIPT]: Escribe un guión de Reel para ayudar a "resolver" (enganchar) sobre el tema del nicho. Debe enganchar a cualquiera planteando OBLIGATORIAMENTE un cuestionamiento/pregunta intrigante en el audio, detallando un texto de introducción visual rápido, un desarrollo estructurado en 5 pasos acelerados o consejos/cosas de referencia para entregar el valor de manera concisa.
                   - [REELS_CAPTION]: Escribe una descripción que afirme firmemente que tú tienes la solución exacta o completa y que deben leerla o ir a leer tu descripción/perfil. Agrega exactamente 4 hashtags.
                   
                3. INSTAGRAM CARUSEL (Frecuencia: 2 diarios):
                   - [CAROUSEL_COVER]: Diseña una frase de portada con un gancho desafiante (ej. "¿A qué le temes?", "Si supieras esto antes...", "Si haces esto eres...").
                   - Genera exactamente 6 diapositivas (slides) consecutivas para aumentar el ego del lector (cada una con una frase de 15 a 25 palabras de longitud) que aporten comparaciones, consejos u opciones sabias.
                     - [CAROUSEL_SLIDE_1]: Debe iniciar o enfocarse con la palabra "Nadie" (ej. "Nadie vendrá a salvar tu jubilación...").
                     - [CAROUSEL_SLIDE_2]: Debe iniciar o enfocarse con la palabra "No hagas" o "Don't do" (ej. "No hagas lo que la mayoría hace...").
                     - [CAROUSEL_SLIDE_3]: Debe iniciar o enfocarse con la palabra "Un sabio dijo" (ej. "Un sabio dijo una vez que la ignorancia...").
                     - [CAROUSEL_SLIDE_4]: Debe iniciar o enfocarse con la palabra "Madurar" (ej. "Madurar es entender que tus ingresos...").
                     - [CAROUSEL_SLIDE_5]: Debe iniciar o enfocarse con la palabra "Yo creía" (ej. "Yo creía que la economía era ajena...").
                     - [CAROUSEL_SLIDE_6]: Debe iniciar o enfocarse con la palabra "Muchos" o "La mayoría" (ej. "Muchos culpan a la suerte por su escasez...").
                   CADA una de las slides debe tener entre 15 y 25 palabras exactas para generar el máximo impacto emocional y de empoderamiento en el lector.
                   
                4. YOUTUBE VIDEO LARGO (Frecuencia: 1 semanal):
                   - [YOUTUBE_SCRIPT]: Genera una poderosa narrativa personal escrita en TERCERA PERSONA. Debe iniciar contando parte de la historia de un creador/usuario con dificultades combinada con mensajes centrados fuertemente en política y finanzas (explicando cómo la política de un país afecta directamente a las finanzas e ingresos de todo ciudadano). Al final, debe guiar el guión para invitar al espectador a iniciar/unirse a una comunidad digital de personas comprometidas que quieren construir un país mejor.
                
                Instrucción de formato crítica:
                Devuelve la respuesta estructurando CADA sección estrictamente con su respectivo tag de inicio de la siguiente manera. No agregues saludos, introducciones ni conclusiones, solo las etiquetas tal cual con sus contenidos:
                [FB_HOOK]
                "tu frase"
                [FB_IMAGE_DESC]
                tu propuesta visual
                [FB_CAPTION]
                tu caption de facebook
                [REELS_SCRIPT]
                tu guión estructurado
                [REELS_CAPTION]
                tu caption de reel
                [CAROUSEL_COVER]
                tu gancho de portada
                [CAROUSEL_SLIDE_1]
                slide 1
                [CAROUSEL_SLIDE_2]
                slide 2
                [CAROUSEL_SLIDE_3]
                slide 3
                [CAROUSEL_SLIDE_4]
                slide 4
                [CAROUSEL_SLIDE_5]
                slide 5
                [CAROUSEL_SLIDE_6]
                slide 6
                [YOUTUBE_SCRIPT]
                tu narrativa en tercera persona sobre política, finanzas y comunidad
            """.trimIndent()

            try {
                val rawResponse = geminiManager.generateSocialContent(
                    prompt = "Nicho solicitado: $niche",
                    systemPrompt = systemPrompt
                )
                
                if (rawResponse.startsWith("Error")) {
                    nicheError = rawResponse
                } else {
                    nicheGeneratedResult = parseNicheResult(rawResponse)
                }
            } catch (e: Exception) {
                nicheError = e.localizedMessage ?: e.message ?: "Error desconocido al invocar Gemini"
            } finally {
                isNicheGenerating = false
            }
        }
    }

    private fun parseNicheResult(raw: String): NicheContentResult {
        fun extractSection(tag: String, fallback: String): String {
            val startTag = "[$tag]"
            val startIdx = raw.indexOf(startTag)
            if (startIdx == -1) return fallback
            val textStart = startIdx + startTag.length
            var nextBracket = raw.indexOf("[", textStart)
            if (nextBracket == -1) {
                nextBracket = raw.length
            }
            return raw.substring(textStart, nextBracket).trim()
        }

        val fbHook = extractSection("FB_HOOK", "\"El verdadero riesgo en una economía cambiante es quedarse esperando soluciones de otros.\"")
        val fbImageDesc = extractSection("FB_IMAGE_DESC", "Un fondo elegante, moderno y minimalista con texturas abstractas de tonos oscuros y destellos dorados.")
        val fbCaption = extractSection("FB_CAPTION", "A menudo nos enfocamos en el trabajo duro diario, pero ignoramos las reglas invisibles que rigen el mercado. El mundo se mueve rápido y el futuro pertenece a quienes toman la iniciativa. ¿Serás un espectador o el protagonista de tu propia historia? #Estrategia #Futuro #Negocios #Desarrollo")
        val reelsScript = extractSection("REELS_SCRIPT", "Audio Gancho: ¿Por qué el 95% trabaja duro pero nunca alcanza su verdadera libertad económica? \nIntro Visual: El secreto del control financiero revelado.\n1. Dependes enteramente de una única fuente de ingresos.\n2. No conoces el impacto diario del interés compuesto.\n3. Dejas tus decisiones financieras al azar.\n4. Gastas para impresionar en lugar de invertir.\n5. Ignoras cómo las leyes estatales influyen en tu bolsillo.\nEvita: Seguir de largo sin metas.")
        val reelsCaption = extractSection("REELS_CAPTION", "La respuesta completa para tomar el control de tu destino está resumida paso a paso en mi perfil. Haz click hoy y empieza a transformar tus resultados. #SocialGrow #LibertadFinanciera #Hacks #Soluciones")
        val carouselCover = extractSection("CAROUSEL_COVER", "¿A qué le temes en esta época?")
        
        val slides = mutableListOf<String>()
        for (i in 1..6) {
            val slideText = extractSection("CAROUSEL_SLIDE_$i", "")
            if (slideText.isNotEmpty()) {
                slides.add(slideText)
            }
        }
        if (slides.isEmpty()) {
            slides.add("Nadie va a construir tus metas; esa es tu responsabilidad exclusiva frente al espejo de tu destino.")
            slides.add("Don't do: no dejes que la falsa comodidad de la rutina nuble la urgente necesidad de crecer hoy.")
            slides.add("Un sabio dijo que el camino hacia la verdadera maestría empieza admitiendo la inmensidad de lo que ignoramos.")
            slides.add("Madurar es comprender que tus ingresos y ahorros dependen del sistema que tanto decides ignorar habitualmente.")
            slides.add("Yo creía que el dinero era solo para gastar, hasta que vi cómo devaluaban mi esfuerzo día tras día.")
            slides.add("Muchos se quejan de la tormenta global, pero muy pocos se deciden a fabricar sus propios paraguas.")
        }

        val youtubeScript = extractSection("YOUTUBE_SCRIPT", "Un joven luchador de nuestra comunidad comenzó su historia vendiendo productos desde una pequeña habitación compartida. Descubrió que por más que se esforzaba, las decisiones políticas estatales y las devaluaciones silenciosas destruían el poder de su salario de la noche a la mañana. Comprendió una lección crucial: las finanzas individuales y la política fiscal están indisolublemente conectadas. No se rindió en silencio. Hoy compartimos su historia porque decidimos iniciar una comunidad digital unida para aprender, crear economías independientes y empoderarnos juntos por el desarrollo real de nuestro país. Únete hoy a nuestro canal.")

        return NicheContentResult(
            facebookHook = fbHook,
            facebookImageDescription = fbImageDesc,
            facebookCaption = fbCaption,
            reelsScript = reelsScript,
            reelsCaption = reelsCaption,
            carouselCover = carouselCover,
            carouselSlides = slides,
            youtubeScript = youtubeScript
        )
    }

    fun generateContentWithAI(prompt: String, platform: String, tone: String) {
        viewModelScope.launch {
            isGeneratingState = true
            aiGeneratorResponse = "Generando contenido optimizado..."

            val systemPrompt = """
                Eres un experto Redactor de Contenidos y Estratega de Redes Sociales (Social Media Manager). 
                Genera un caption creativo adaptado estrictamente al tono de voz y a las directrices de la plataforma seleccionada.
                
                Red social objetivo: $platform
                Tono de voz solicitado: $tone
                
                Sigue estas reglas estrictamente:
                1. Da respuestas directas que el usuario pueda copiar y usar. No añadas notas del tipo "Aquí tienes tu copy" antes de la respuesta principal.
                2. Si la plataforma es 'X / Twitter', asegúrate de que el texto no supere los 280 caracteres y sea muy conciso, con 0-2 hashtags.
                3. Si es 'Instagram/Facebook', puedes usar saltos de línea elegantes, emojis decorativos y un bloque de hasta 5 hashtags relevantes al final.
                4. Si es 'LinkedIn', mantén un tono profesional, estructurado en párrafos cortos o listas con viñetas, enfocándote en valor corporativo, desarrollo y networking.
                5. Si es 'TikTok', el texto debe ser extremadamente corto y gancho (hook) inicial fuerte para guiar hacia un video, acompañado de hashtags de tendencias.
                6. Adapta de forma inteligente el lenguaje a la temática indicada por el usuario.
            """.trimIndent()

            val aiResponse = geminiManager.generateSocialContent(prompt, systemPrompt)
            aiGeneratorResponse = aiResponse
            isGeneratingState = false
        }
    }

    // Factory Class representation for Android ViewModels
    class Factory(
        private val application: Application,
        private val repository: SocialRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class NicheContentResult(
    val facebookHook: String,
    val facebookImageDescription: String,
    val facebookCaption: String,
    val reelsScript: String,
    val reelsCaption: String,
    val carouselCover: String,
    val carouselSlides: List<String>,
    val youtubeScript: String
)
