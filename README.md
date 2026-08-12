# 🎬 CineSound

**Recomendações de filmes e séries baseadas em você — com trilha sonora e onde assistir, tudo em um só lugar.**

![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)
![Plataforma](https://img.shields.io/badge/plataforma-Android-3DDC84?logo=android&logoColor=white)
![Linguagem](https://img.shields.io/badge/linguagem-Java-orange?logo=openjdk&logoColor=white)
![Backend](https://img.shields.io/badge/backend-Firebase-FFCA28?logo=firebase&logoColor=black)
![API](https://img.shields.io/badge/dados-TMDB-01B4E4?logo=themoviedatabase&logoColor=white)

> ⚠️ **Projeto em desenvolvimento.** O CineSound ainda **não está 100% finalizado** — as funcionalidades principais já estão definidas e em construção, mas o app está passando por testes e correção de bugs antes da versão final. Veja a seção [Status do projeto](#-status-do-projeto) para mais detalhes.

---

## 📖 Sobre o projeto

O **CineSound** é um aplicativo Android para recomendação personalizada de filmes e séries. O diferencial é simples: as recomendações não vêm apenas de popularidade genérica — elas são construídas a partir das **próprias classificações do usuário** (Amei / Gostei / Não gostei / Odiei). Além disso, cada título traz informações extras que a maioria dos apps de catálogo não oferece de forma integrada, como **trilha sonora** e **onde assistir** (Netflix, Prime Video, Disney+, etc).

Este é um projeto acadêmico desenvolvido para uma disciplina de desenvolvimento mobile, com foco em ser **interessante e escalável** — a escalabilidade é garantida pelo uso do Firebase, que suporta de 10 a 10.000 usuários sem qualquer mudança de infraestrutura.

O app é **individual**: não há funcionalidades sociais (seguir amigos, ver o que outros assistiram). Toda a experiência gira em torno das preferências de cada usuário.

---

## ✨ Funcionalidades

| Funcionalidade | Onde aparece | Descrição |
|---|---|---|
| **Login e cadastro** | Telas iniciais | E-mail e senha, sessão persistente, recuperação de senha por e-mail |
| **Recomendações** | Aba Home | Sugestões baseadas nos gêneros dos títulos que o usuário marcou como *Amei* ou *Gostei*, com filtro Todos / Filmes / Séries |
| **Mais curtidos da semana** | Aba Em alta | Ranking semanal (trending) com filtro por tipo |
| **Catálogo geral** | Aba Catálogo | Lista completa com busca por nome e filtros avançados: tipo, gênero (múltipla seleção), ano e nota mínima |
| **Busca** | Aba Catálogo | Pesquisa por nome em tempo real, com pôster, título, ano e tipo |
| **Detalhes do título** | Tela de detalhes | Pôster, sinopse, nota, gêneros, elenco, trilha sonora e onde assistir |
| **Classificação** | Tela de detalhes | 4 níveis — Amei ❤️ · Gostei 👍 · Não gostei 👎 · Odiei 💔 — salvos no Firestore |
| **Onde assistir** | Tela de detalhes | Plataformas de streaming disponíveis no Brasil |
| **Trilha sonora** | Tela de detalhes | Músicas/vídeos associados ao filme ou série |
| **Perfil** | Aba Perfil | Avatar, dados do usuário e histórico organizado pelas 4 categorias de classificação |

### 🧠 Como funciona a recomendação

1. O app busca no Firestore todas as classificações do usuário marcadas como `amei` ou `gostei`.
2. Extrai os gêneros desses títulos e identifica os **3 gêneros mais frequentes**.
3. Esses gêneros são usados para consultar `/discover/movie` e `/discover/tv` na API do TMDB.
4. O resultado vira as recomendações personalizadas exibidas na Home.
5. Se o usuário ainda não classificou nada, a Home mostra o trending semanal como fallback.

---

## 🛠️ Tecnologias

| Tecnologia | Uso no projeto |
|---|---|
| **Java** | Linguagem principal do app |
| **Android Studio** | IDE de desenvolvimento |
| **Firebase Authentication** | Login, cadastro e recuperação de senha |
| **Firebase Firestore** | Armazenamento de perfis, classificações e histórico |
| **TMDB API** | Dados de filmes, séries, elenco, nota, trilha sonora e onde assistir |
| **Retrofit** | Consumo da API do TMDB |
| **Glide** | Carregamento de imagens/pôsteres |
| **Navigation (Jetpack)** | Navegação entre telas |
| **Material Design** | Padrão visual do app |
| **Git + GitHub** | Versionamento e colaboração |

---

## 🏗️ Arquitetura

```
app/java/com.exemplo.cinesound/
├── ui/
│   ├── login/       → LoginActivity, CadastroActivity, RecuperarSenhaActivity
│   ├── home/        → HomeFragment
│   ├── emalta/      → EmAltaFragment
│   ├── catalogo/    → CatalogoFragment, FiltroBottomSheet
│   ├── perfil/      → PerfilFragment
│   └── detalhes/    → DetalhesActivity
├── adapters/         → TituloAdapter, ElencoAdapter
├── models/           → TituloItem, ResultadoTmdb, ItemTmdb, DetalheTmdb
├── repository/        → FirebaseRepository, TmdbRepository
├── network/          → RetrofitClient, TmdbService, Callback
└── MainActivity.java
```

Navegação estruturada em uma `MainActivity` com Bottom Navigation Bar contendo 4 Fragments (Home, Em alta, Catálogo, Perfil). As telas de autenticação e a tela de detalhes são Activities independentes.

### Contrato de dados — `TituloItem`

Toda a informação de filme/série que trafega entre backend e frontend passa por essa classe única, compartilhada entre as áreas de Firebase e API:

```java
public class TituloItem {
    public int tmdbId;
    public String titulo;
    public String poster;
    public String tipo;                 // "filme" ou "serie"
    public List<Integer> generos;
    public double notaTmdb;
    public String classificacao;        // "amei" | "gostei" | "nao_gostei" | "odiei"
    public Timestamp dataClassificacao;
}
```

> Valores de `tipo` e `classificacao` seguem sempre minúsculo, sem acento e sem espaço — qualquer variação quebra a lógica de recomendação e os filtros de perfil.

---

## 🔌 API TMDB — endpoints utilizados

| Endpoint | Uso |
|---|---|
| `/search/multi?query={nome}` | Busca por nome |
| `/trending/all/week` | Ranking semanal |
| `/discover/movie` | Catálogo de filmes com filtros / recomendações |
| `/discover/tv` | Catálogo de séries com filtros / recomendações |
| `/movie/{id}` e `/tv/{id}` | Detalhes completos |
| `/movie/{id}/credits` | Elenco |
| `/movie/{id}/watch/providers` | Onde assistir |
| `/movie/{id}/videos` | Trilha sonora |
| `/genre/movie/list` | Lista de gêneros para filtros |

Todas as chamadas usam `language=pt-BR` como padrão.

---

## 🚀 Como rodar o projeto

> ⚠️ Pré-requisito: você precisa de uma API Key própria do [TMDB](https://www.themoviedb.org/documentation/api) e de um projeto Firebase configurado.

1. Clone o repositório:
   ```bash
   git clone https://github.com/ayranboudoux/Project-Cinesound.git
   ```
2. Abra o projeto no **Android Studio**.
3. Adicione sua API Key do TMDB no arquivo `local.properties` (nunca no código versionado):
   ```properties
   TMDB_API_KEY=sua_chave_aqui
   ```
4. Adicione o arquivo `google-services.json` do seu projeto Firebase na pasta `app/`.
5. Sincronize o Gradle e rode o app em um emulador ou dispositivo físico.

---

## 📌 Status do projeto

O CineSound está **em desenvolvimento ativo** e ainda não é uma versão final. Neste momento, o foco está em:

- [ ] Testes no emulador e em dispositivo físico
- [ ] Correção de bugs
- [ ] Ajustes finos de UI/UX
- [ ] Documentação final e preparação da apresentação

As funcionalidades principais (login, recomendações, catálogo, detalhes, classificação e perfil) já estão mapeadas e em construção, mas podem existir telas incompletas, fluxos quebrados ou dados de teste enquanto o desenvolvimento avança.

---

## 👥 Equipe

| Nome | Área | Responsabilidades |
|---|---|---|
| **Ayran** | Backend — Firebase | Authentication, Firestore, regras de segurança, lógica de recomendação |
| **Rayan** | Backend — API TMDB | Retrofit, busca, trending, catálogo, detalhes, onde assistir, trilha sonora |
| **Kauan** | Frontend | Todas as telas do app, navegação, componentes visuais, Material Design |

### Fluxo de trabalho

- Cada pessoa trabalha no seu próprio branch (`feature/banco-de-dados`, `feature/api-tmdb`, `feature/telas`) — nunca direto na `main`.
- Ao finalizar uma funcionalidade, abre-se um Pull Request revisado pelo time antes do merge.
- Rotina diária: `git pull origin main` antes de começar, e `git add . && git commit -m "descrição" && git push origin {branch}` ao terminar.

---

## 🎓 Contexto acadêmico

Projeto desenvolvido como trabalho de faculdade, com o requisito de ser **interessante e escalável**. A escalabilidade é sustentada pelo Firebase (suporta de 10 a 10.000 usuários sem mudança de infraestrutura), e a integração com a API do TMDB — usada por apps reais do mercado — reforça a proposta de um produto com arquitetura profissional: backend, banco de dados e frontend desenvolvidos de forma independente, conectados por um contrato de dados bem definido.

---

*CineSound — desenvolvido por Ayran, Rayan e Kauan.*
