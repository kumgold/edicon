# EdiCon
<p align="left">
<img src="https://img.shields.io/badge/Kotlin-1.9.0-7F52FF?style=flat&logo=kotlin&logoColor=white"/>
<img src="https://img.shields.io/badge/Android-Jetpack_Compose-3DDC84?style=flat&logo=android&logoColor=white"/>
<img src="https://img.shields.io/badge/Architecture-MVI-blue?style=flat"/>
<img src="https://img.shields.io/badge/API-Pixabay-orange?style=flat"/>
</p>

# 🎬 Edicon (Edit + Icon)

Edicon은 Pixabay API를 활용하여 고화질의 이미지와 동영상을 검색하고 감상할 수 있는 안드로이드 애플리케이션입니다.
최신 안드로이드 기술 스택인 Jetpack Compose와 MVI 아키텍처를 기반으로 개발되었으며, Paging3를 통한 무한 스크롤과 ExoPlayer를 이용한 부드러운 동영상 재생 경험을 제공합니다.

# ✨ 주요 기능 (Key Features)
- 미디어 검색: 키워드를 입력하여 Pixabay의 방대한 이미지와 동영상을 검색할 수 있습니다.
- 무한 스크롤 (Infinite Scrolling): Paging3 라이브러리를 적용하여 끊김 없는 데이터 로딩을 지원합니다.
- 동영상 재생: ExoPlayer를 활용하여 검색된 동영상을 앱 내에서 바로 재생합니다.
- 즐겨찾기 (Bookmarks): Room Database를 사용하여 마음에 드는 미디어를 로컬에 저장하고 관리합니다.
- 반응형 UI: Jetpack Compose를 사용하여 다양한 화면 크기에 대응하는 직관적인 UI를 구성했습니다.

# 🛠 기술 스택 (Tech Stack)

## 🏗 Architecture
- MVI (Model-View-Intent): 단방향 데이터 흐름(Unidirectional Data Flow)을 통해 상태 관리를 명확히 하고, UI와 비즈니스 로직을 분리했습니다.
- Clean Architecture: Presentation, Domain, Data 레이어로 관심사를 분리하여 유지보수성을 높였습니다.

## 📚 Libraries
|Category|Technology|
|--------|----------|
|UI|Jetpack Compose (Material3)|
|Asynchronous|Coroutines, Flow|
|Network|Retrofit2, OkHttp3|
|Image Loading|Coil|
|Video Player|ExoPlayer (Media3)|
|Local Database|Room|
|Pagination|Paging 3|
|Dependency Injection|Hilt|

## 💡 구현 상세 (Implementation Details)
### 1. MVI Pattern 적용
사용자의 의도(Intent)를 받아 상태(State)를 변경하고, 부수 효과(Side Effect)를 처리하는 MVI 패턴을 적용했습니다.
- Intent: 검색어 입력, 아이템 클릭, 즐겨찾기 버튼 클릭 등 사용자의 행위.
- State: 로딩 중, 데이터 성공, 에러 발생 등 화면에 보여질 상태.
- Side Effect: Toast 메시지, 화면 이동 등 일회성 이벤트.

### 2. Paging3 & Network Caching
대량의 이미지/동영상 데이터를 효율적으로 처리하기 위해 Paging3를 사용했습니다. 네트워크 요청을 줄이고 사용자 경험을 향상시키기 위해 적절한 캐싱 전략을 도입했습니다.

### 3. ExoPlayer 최적화
리스트 내에서의 동영상 미리보기 및 상세 화면에서의 재생을 위해 ExoPlayer를 최적화하여 적용했습니다. 생명주기(Lifecycle)에 따른 리소스 해제를 통해 메모리 누수를 방지했습니다.

## 🚀 시작하기 (Getting Started)
이 프로젝트를 실행하기 위해서는 Pixabay API Key가 필요합니다.

1. 프로젝트를 클론합니다.
```Bash
git clone https://github.com/kumgold/Edicon.git
```
2. Pixabay API에서 API Key를 발급받습니다.
3. 프로젝트 루트의 local.properties 파일에 키를 추가합니다.
```Properties
PIXABAY_API_KEY=your_api_key_here
```
4. Android Studio에서 프로젝트를 Sync하고 실행합니다.

## 📂 폴더 구조 (Package Structure)
```Text
com.example.edicon
├── data           # API 호출, Room DB, Repository 구현체
├── domain         # UseCase, Repository 인터페이스, Model
├── presentation   # UI (Compose), ViewModel, Contract (MVI)
├── di             # 의존성 주입 모듈
└── common         # 확장 함수, 유틸리티
```

## ⚖️ License
```Code
Copyright 2024 [kumgold]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
