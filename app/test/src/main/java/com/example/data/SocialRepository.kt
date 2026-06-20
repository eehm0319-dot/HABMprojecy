package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

class SocialRepository(private val socialDao: SocialDao) {

    val allPosts: Flow<List<SocialPost>> = socialDao.getAllPosts()
    val allProfiles: Flow<List<SocialProfile>> = socialDao.getAllProfiles()
    val allAssets: Flow<List<AssetItem>> = socialDao.getAllAssets()

    suspend fun getPostById(id: Int): SocialPost? {
        return socialDao.getPostById(id)
    }

    suspend fun insertPost(post: SocialPost): Long {
        return socialDao.insertPost(post)
    }

    suspend fun updatePost(post: SocialPost) {
        socialDao.updatePost(post)
    }

    suspend fun deletePost(post: SocialPost) {
        socialDao.deletePost(post)
    }

    suspend fun deletePostById(id: Int) {
        socialDao.deletePostById(id)
    }

    suspend fun updateProfile(profile: SocialProfile) {
        socialDao.updateProfile(profile)
    }

    suspend fun insertAsset(asset: AssetItem): Long {
        return socialDao.insertAsset(asset)
    }

    suspend fun deleteAsset(asset: AssetItem) {
        socialDao.deleteAsset(asset)
    }

    suspend fun checkAndSeedDatabase() {
        val existingProfiles = socialDao.getAllProfiles().firstOrNull() ?: emptyList()
        if (existingProfiles.isEmpty()) {
            // Seed Profiles
            val initialProfiles = listOf(
                SocialProfile("instagram", "socialgrow_es", "SocialGrow Instagram", "#E1306C", true, 15420),
                SocialProfile("twitter", "SocialGrowAI", "SocialGrow Twitter / X", "#1DA1F2", true, 8930),
                SocialProfile("linkedin", "socialgrow-solutions", "SocialGrow LinkedIn", "#0077B5", true, 4210),
                SocialProfile("tiktok", "socialgrow_latam", "SocialGrow TikTok", "#000000", true, 31200)
            )
            socialDao.insertProfiles(initialProfiles)

            // Seed initial Assets
            val now = System.currentTimeMillis()
            val initialAssets = listOf(
                AssetItem(0, "Banner Corporativo Teal", "banner_brand_teal", "image/png", "marketing,marca,negocios", "IMAGE", now),
                AssetItem(0, "Plantilla de Copy: Call To Action", "🤔 ¿Quieres multiplicar tu engagement orgánico? Sigue este hilo con 5 hacks prácticos... 👇", "text/plain", "copywriting,cta,estrategia", "TEXT_TEMPLATE", now - 3600000),
                AssetItem(0, "Video Mockup Intro Reels", "visual_video_nature", "video/mp4", "reels,video,estilo", "VIDEO", now - 7200000)
            )
            for (asset in initialAssets) {
                socialDao.insertAsset(asset)
            }

            // Seed initial posts
            val oneDayMs = 24 * 60 * 60 * 1000L
            val initialPosts = listOf(
                SocialPost(
                    id = 0,
                    title = "Lanzamiento de SocialGrow Mobile",
                    content = "¡Lanzamiento Oficial de Nuestra App! 🚀 Diseña, programa y optimiza tus posts con IA directamente desde tu celular. Disponible para agencias y creadores. #SocialMedia #IA #SMM #Emprendedores",
                    scheduledTime = now - (2 * oneDayMs),
                    platforms = "instagram,twitter,linkedin",
                    mediaUri = "banner_brand_teal",
                    status = "PUBLISHED",
                    isEvergreen = true,
                    clicks = 254,
                    likes = 482,
                    comments = 56,
                    shares = 32
                ),
                SocialPost(
                    id = 0,
                    title = "5 Tips de IA para Captions",
                    content = "¿Saturado al escribir copys en redes? 🧠 Aquí tienes 5 tips rápidos con Inteligencia Artificial para multiplicar tu engagement esta semana: \n1. Haz preguntas intrigantes.\n2. Adapta la longitud.\n3. Añade llamadas a la acción.\n4. Segmenta con hashtags nicho.\n5. Revisa el tono con SocialGrow.\n¡Aplica esto hoy! #Innovation #CreatorEconomy #IA #Tips",
                    scheduledTime = now + (oneDayMs / 2), // Scheduled within tomorrow
                    platforms = "instagram,linkedin",
                    mediaUri = null,
                    status = "APPROVED",
                    isEvergreen = false
                ),
                SocialPost(
                    id = 0,
                    title = "Formatos de TikTok en Tendencia",
                    content = "Los videos de menos de 15 segundos con overlays interactivos están dominando la pestaña 'Para ti' esta semana. Asegúrate de usar subtítulos de alto contraste y colores vibrantes. 💪",
                    scheduledTime = now + (2 * oneDayMs),
                    platforms = "tiktok",
                    mediaUri = null,
                    status = "REVIEW",
                    isEvergreen = false
                ),
                SocialPost(
                    id = 0,
                    title = "Estrategias de Reposting de Evergreen",
                    content = "Tu contenido valioso merece una segunda vida. El 70% de tus seguidores actuales no vio tu mejor post de hace 3 meses. Habilita la queue evergreen en SocialGrow y automatiza la publicación constante.",
                    scheduledTime = now + (4 * oneDayMs),
                    platforms = "twitter,linkedin",
                    mediaUri = null,
                    status = "DRAFT",
                    isEvergreen = true
                )
            )

            for (p in initialPosts) {
                socialDao.insertPost(p)
            }
        }
    }
}
