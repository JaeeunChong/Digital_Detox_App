# Digital Detox App — OFF:ON

프로그램설계방법론 팀 프로젝트

스마트폰 사용 시간을 줄이고 현실 활동을 실천하면 가상의 마을과 캐릭터가 성장하는 디지털 디톡스 Android 애플리케이션입니다.

## 1. 개발 환경

| 항목 | 내용 |
|---|---|
| IDE | Android Studio |
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| Minimum SDK | API 26 (Android 8.0) |
| Build System | Gradle Kotlin DSL |
| Navigation | Navigation Compose |
| Package Name | `com.team.digitaldetox` |
| Version Control | Git / GitHub |

Android 프로젝트는 하나의 `app` 모듈로 구성되어 있으며, 기능별 패키지를 구분해 개발합니다.

## 2. 프로젝트 구조

```text
app/src/main/
├── java/com/team/digitaldetox/
│   ├── MainActivity.kt
│   │
│   ├── feature/
│   │   ├── onboarding/
│   │   │   └── OnboardingScreens.kt
│   │   ├── home/
│   │   │   └── HomeScreen.kt
│   │   ├── usage/
│   │   │   └── UsageScreen.kt
│   │   ├── activity/
│   │   │   └── ActivityScreen.kt
│   │   ├── world/
│   │   │   └── WorldScreen.kt
│   │   └── interaction/
│   │       └── InteractionScreen.kt
│   │
│   ├── navigation/
│   │   └── AppNavGraph.kt
│   │
│   ├── data/
│   │
│   └── ui/
│       ├── components/
│       │   └── PlaceholderScreen.kt
│       └── theme/
│
└── res/
    └── drawable/
```

`data` 등 일부 패키지는 향후 개발을 위한 공간이며, 아직 실제 데이터베이스가 구현된 것은 아닙니다.

## 3. 기능별 담당 구분

| 담당 | 기능 | 개발 위치 |
|---|---|---|
| A | 디지털 디톡스 및 사용 데이터 수집 | `feature/usage` |
| B | 현실 활동 및 미션 | `feature/activity` |
| C | 마을 및 캐릭터 성장 | `feature/world` |
| D | 캐릭터 상호작용 및 개인화 | `feature/interaction` |
| 공통 | 온보딩, 홈, 화면 이동, 공통 UI | `onboarding`, `home`, `navigation`, `ui` |

공통 파일을 수정해야 한다면 팀원들과 먼저 협의합니다.

## 4. 현재 구현 상태

### 구현 및 실행 확인

- Android Studio 공통 프로젝트 구성
- Kotlin 및 Jetpack Compose 환경 설정
- 기능별 패키지 및 기본 화면 생성
- Navigation을 통한 화면 이동
- 온보딩 화면 3개 구현
    - 취침 시간 및 기상 시간 설정
    - 허용 앱 선택 및 체크 UI
    - 캐릭터 선택 및 닉네임 입력
- 공통 테스트 홈 화면과 A·B·C·D 화면 연결
- 에뮬레이터 기본 실행 및 화면 이동 확인

### 현재 미구현 및 개발 예정

- 온보딩 설정값 영구 저장
- 실제 스마트폰 사용 기록 측정 및 앱 제한
- 걸음 수 측정과 활동 보상 계산
- 마을 성장 및 주민 상호작용 기능
- Room 데이터베이스
- 최종 Figma UI 적용

**현재 온보딩은 기본 UI와 상호작용을 구현한 프로토타입입니다.**

닉네임의 한글 입력은 테스트하였으며, 입력 문제 확인을 위해 10자 제한은 임시로 해제한 상태입니다. 추후 정상적인 글자 수 검증을 다시 적용해야 합니다.

## 5. 화면 이동 구조

```text
MainActivity
    ↓
AppNavGraph
    ↓
취침 시간 설정
    ↓
허용 앱 선택
    ↓
캐릭터 및 닉네임 선택
    ↓
HomeScreen (임시 홈)
    ├── A. UsageScreen
    ├── B. ActivityScreen
    ├── C. WorldScreen
    └── D. InteractionScreen
```

현재 앱은 실행 시 온보딩부터 시작합니다.

추후 온보딩 완료 여부를 저장해 재실행 시 홈 화면으로 이동하도록 구현할 예정입니다.

## 6. 프로젝트 실행 방법

1. GitHub Repository를 Clone합니다.

```bash
git clone https://github.com/JaeeunChong/Digital_Detox_App.git
```

2. Android Studio에서 Clone한 프로젝트를 엽니다.
3. Gradle Sync를 완료합니다.
4. Android 에뮬레이터 또는 실제 기기를 선택합니다.
5. Run을 눌러 앱을 실행합니다.

각자 별도의 Android 프로젝트를 새로 생성하지 않습니다.

## 7. GitHub 협업 규칙

- `main` 브랜치에 직접 Push하지 않습니다.
- 각 담당자는 기능별 브랜치에서 작업합니다.
- 작업 단위별로 Commit합니다.
- 변경한 코드는 Pull Request를 통해 병합합니다.
- PR 병합 전 팀원 최소 1명이 변경 사항을 확인합니다.
- 공통 파일 변경 시 사전에 공유합니다.

### 담당 브랜치

- A: `feature/usage-tracking`
- B: `feature/activity-mission`
- C: `feature/world`
- D: `feature/interaction`

## 8. 공통 파일 수정 시 주의사항

다음 파일은 다른 담당자의 기능에도 영향을 줄 수 있습니다.

- `MainActivity.kt`
- `AppNavGraph.kt`
- `build.gradle.kts`
- `AndroidManifest.xml`
- `ui/theme` 내부 파일
- 향후 추가될 공통 데이터 모델 및 DB 관련 파일

기존 코드 전체를 덮어쓰기 전에 다른 담당자의 기능에 영향을 주는지 확인합니다.

## 9. Figma 디자인

현재 Figma는 초기 디자인 시안이며 최종 확정본이 아닙니다.

Figma 링크:

https://www.figma.com/design/z0OEO4y0agzgWqZGFzm4Xz/

현재 구현된 UI는 기본 동작 확인을 위한 것으로, 향후 Figma의 배경, 패널, 버튼, 폰트 및 픽셀아트 리소스를 적용할 예정입니다.

## 10. ChatGPT 등을 활용한 개발 안내

AI를 활용하여 코드를 작성하거나 수정할 경우 다음 사항을 지켜 주세요.

- 기존 Android 프로젝트의 패키지 및 폴더 구조를 유지합니다.
- Kotlin + Jetpack Compose를 기준으로 작성합니다.
- 다른 담당자의 코드를 임의로 삭제하지 않습니다.
- 새 라이브러리가 필요한 경우 추가 이유와 수정 파일을 확인합니다.
- 공통 Navigation 변경이 필요하면 팀원과 먼저 협의합니다.
- AI가 작성한 코드는 Android Studio에서 직접 빌드하고 테스트합니다.
- README는 프로젝트 설명 문서이므로, 실제 코드 수정 요청 시 관련 `.kt` 파일도 함께 제공하는 것이 좋습니다.

**개발 원칙: 기존 공통 구조를 유지하면서 각 담당 기능을 독립적으로 구현하고, GitHub PR로 통합합니다.**
