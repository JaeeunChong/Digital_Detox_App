# OFF:ON — Digital Detox App

**프로그램설계방법론 | Android 팀 프로젝트**

OFF:ON은 스마트폰 사용 시간을 줄이고 현실 활동을 장려하는 디지털 디톡스 애플리케이션입니다.

사용자가 디지털 기기 사용을 줄이고 걸음 수, 활동, 미션 등을 통해 보상을 얻으면 가상의 마을과 캐릭터가 성장하는 시스템을 목표로 합니다.

> **현재 개발 단계: 초기 Android 프로젝트 및 온보딩 UI 프로토타입**
>
> README에 소개된 모든 기능이 구현된 것은 아닙니다. 실제 구현 여부는 아래의 개발 현황을 확인해 주세요.

---

## 1. 개발 환경

| 항목 | 내용 |
|---|---|
| 개발 도구 | Android Studio |
| 개발 언어 | Kotlin |
| UI 프레임워크 | Jetpack Compose |
| 최소 지원 버전 | Android 8.0 / API 26 |
| 빌드 시스템 | Gradle Kotlin DSL |
| 화면 이동 | Navigation Compose |
| 패키지명 | `com.team.digitaldetox` |
| 형상 관리 | Git / GitHub |
| 디자인 | Figma |

**중요:** 팀원들은 별도의 Android 프로젝트를 새로 만들지 않고, 이 저장소를 Clone해서 개발합니다.

Android Studio, Gradle 및 라이브러리의 세부 버전은 각자 임의로 통일하지 말고 저장소에 포함된 Gradle 설정을 기준으로 사용합니다.

---

## 2. 프로젝트 폴더 구조

```text
app/src/main/
├── java/com/team/digitaldetox/
│   ├── MainActivity.kt
│   │
│   ├── navigation/
│   │   └── AppNavGraph.kt
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
│   ├── data/
│   │
│   └── ui/
│       ├── components/
│       │   └── PlaceholderScreen.kt
│       └── theme/
│
├── AndroidManifest.xml
│
└── res/
    ├── drawable/
    ├── mipmap-*/
    └── values/
```

### 주요 파일 설명

| 파일 | 역할 |
|---|---|
| `MainActivity.kt` | Android 앱 진입점 |
| `AppNavGraph.kt` | 앱 전체 화면 이동 관리 |
| `HomeScreen.kt` | 현재 공통 테스트 홈 화면 |
| `OnboardingScreens.kt` | 초기 사용자 설정 화면 |
| `PlaceholderScreen.kt` | 미구현 기능용 임시 화면 UI |
| `ui/theme/` | 공통 색상, 테마, 글꼴 설정 |
| `res/drawable/` | 캐릭터, 로고 등 이미지 리소스 |
| `data/` | 향후 공통 데이터 모델 및 저장 기능을 위한 영역 |

현재 `data/`의 구체적인 데이터 구조와 저장 방식은 확정된 상태가 아닙니다. 담당자들이 독립적으로 데이터베이스를 만들기 전에 공통 데이터 구조를 협의해야 합니다.

---

## 3. 기능별 담당 영역

| 담당 | 기능 | 작업 위치 | 브랜치 |
|---|---|---|---|
| A | 디지털 디톡스 및 스마트폰 사용 기록 | `feature/usage/` | `feature/usage-tracking` |
| B | 걸음 수, 현실 활동 및 미션 | `feature/activity/` | `feature/activity-mission` |
| C | 마을 및 캐릭터 성장 | `feature/world/` | `feature/world` |
| D | 캐릭터 상호작용 및 개인화 | `feature/interaction/` | `feature/interaction` |
| 공통 | 온보딩, 홈, 화면 이동, 공통 UI | `onboarding/`, `home/`, `navigation/`, `ui/` | 협의 후 작업 |

각 담당자는 본인 기능의 패키지를 중심으로 개발합니다.

**폴더가 구분되어 있다고 해서 기능이 자동으로 연결되는 것은 아닙니다.** 다른 담당 기능과 데이터를 주고받거나 공통 화면에 연결해야 한다면 팀원들과 먼저 인터페이스를 협의해 주세요.

---

## 4. 현재 구현 상태

### 구현 완료 — 기본 UI 및 실행 환경

- Android Studio 공통 프로젝트 생성
- Kotlin + Jetpack Compose 환경 설정
- A·B·C·D 담당별 패키지 및 기본 화면 생성
- Navigation Compose를 이용한 화면 이동
- 취침 시간 및 기상 시간 설정 UI
- 허용 앱 선택 UI
- 캐릭터 선택 및 닉네임 입력 UI
- 로고 및 일부 픽셀아트 이미지 리소스
- 공통 테스트 홈 화면에서 A·B·C·D 화면 이동

온보딩 3단계와 홈 화면 이동, 한글 닉네임 입력은 초기 테스트를 완료했습니다.

### 아직 구현되지 않았거나 추가 작업이 필요한 기능

- 실제 스마트폰 사용 시간 및 앱별 사용 기록 수집
- 실제 앱 차단·제한 기능
- 센서를 이용한 걸음 수 측정
- 활동/미션 달성 판정 및 보상 지급
- 마을 성장 및 캐릭터 상호작용 로직
- 사용자 설정 및 게임 데이터의 영구 저장
- 앱 재실행 시 온보딩 완료 여부 판별
- 최종 Figma 디자인 적용
- 담당 기능 간 데이터 연동

### 현재 온보딩 관련 주의사항

1. 취침·기상 시간 설정과 허용 앱 선택은 현재 기본 UI 단계입니다.
2. 허용 앱 목록은 실제 설치된 앱이나 Android 시스템의 앱 허용 목록과 연동된 상태가 아닙니다.
3. 닉네임과 캐릭터 정보는 현재 임시 상태로 관리하며, 앱을 완전히 종료한 뒤에도 유지되는 영구 저장 기능은 없습니다.
4. 한글 입력 문제를 확인하는 과정에서 닉네임 최대 10자 제한을 임시 해제했습니다. 추후 한글 조합을 방해하지 않는 방식으로 제한을 다시 적용해야 합니다.
5. 현재 앱 시작 화면은 온보딩이며, 온보딩 완료 여부 저장 기능은 추후 구현해야 합니다.

**UI가 표시된다는 이유만으로 실제 기능이나 데이터 저장까지 구현되었다고 판단하지 마세요.**

---

## 5. 현재 화면 이동 구조

```text
MainActivity
    │
    ▼
AppNavGraph
    │
    ▼
취침 / 기상 시간 설정
    │
    ▼
허용 앱 선택
    │
    ▼
캐릭터 선택 및 닉네임 입력
    │
    ▼
HomeScreen (현재 임시 홈)
    │
    ├── UsageScreen         (A)
    │
    ├── ActivityScreen      (B)
    │
    ├── WorldScreen         (C)
    │
    └── InteractionScreen   (D)
```

화면 이동은 `AppNavGraph.kt`에서 관리합니다.

담당 화면을 개발한다고 해서 `MainActivity.kt`에서 새로운 화면을 직접 시작하거나, 기존 `AppNavGraph.kt` 전체를 새 코드로 교체하지 마세요.

기존 Navigation 구조와 충돌하지 않도록 필요한 화면 연결 부분만 협의하여 수정해야 합니다.

---

## 6. 프로젝트 처음 실행하는 방법

### 처음 참여하는 팀원

**1. GitHub 저장소 Clone**

```bash
git clone https://github.com/JaeeunChong/Digital_Detox_App.git
```

**2. Android Studio에서 Clone한 폴더 열기**

- `File → Open`
- Clone한 `Digital_Detox_App` 폴더 선택
- Gradle Sync 완료 확인
- Android 에뮬레이터 또는 실제 기기 선택
- Run 실행

**3. 실행 확인**

- 온보딩 화면 표시
- 취침 시간 → 허용 앱 → 캐릭터 선택 이동
- 닉네임 입력 및 홈 화면 이동
- A·B·C·D 기본 화면 이동

Clone한 프로젝트가 정상 실행되는 것을 확인한 뒤 담당 기능을 개발합니다.

**별도로 프로젝트를 새로 생성하거나 기존 프로젝트 안에 새로운 Android 프로젝트를 중첩하지 마세요.**

---

## 7. GitHub 브랜치 협업 규칙

### 기본 원칙

- `main`은 팀 전체가 사용하는 안정적인 공통 브랜치입니다.
- 기능 개발은 담당자별 브랜치에서 진행합니다.
- 구현 완료 후 Pull Request(PR)로 `main`에 병합합니다.
- PR 병합 전 최소 1명의 팀원 검토를 원칙으로 합니다.
- `main`에 직접 Push하지 않습니다.
- 한 명이 공통 파일 전체를 덮어써서 다른 팀원의 작업을 삭제하지 않도록 주의합니다.

현재 GitHub의 브랜치 보호 설정과 관계없이 위 사항을 **팀 내부 협업 규칙**으로 적용합니다.

### 브랜치란?

Branch는 동일한 프로젝트를 여러 명이 독립적으로 수정하기 위한 작업 공간입니다.

예를 들어 A 담당자가 사용 시간 측정 기능을 개발하더라도, A의 작업 브랜치를 `main`에 병합하기 전까지 다른 팀원들의 공통 코드가 자동으로 변경되지 않습니다.

### 처음 담당 브랜치 만들기

먼저 `main`을 최신 상태로 만듭니다.

```bash
git switch main
git pull origin main
```

그다음 본인 브랜치를 생성합니다.

```bash
git switch -c feature/usage-tracking
```

위 명령어는 A 담당자의 예시입니다.

다른 담당자는 자신의 브랜치 이름으로 바꿔서 사용합니다.

**이미 브랜치가 존재한다면 새로 만들지 않고 기존 브랜치로 이동합니다.**

```bash
git switch feature/usage-tracking
```

### 작업 내용을 GitHub에 올리기

```bash
git status
git add .
git commit -m "feat: implement usage tracking"
git push -u origin feature/usage-tracking
```

브랜치를 최초로 Push할 때는 `-u origin 브랜치명`을 사용하고, 이후에는 보통 `git push`로 업데이트할 수 있습니다.

`git add .` 실행 후 반드시 `git status`로 업로드 대상 파일을 확인합니다.

### Pull Request 생성하기

1. GitHub 저장소의 `Pull requests`로 이동합니다.
2. `New pull request`를 선택합니다.
3. `base: main`과 `compare: 본인 브랜치`를 확인합니다.
4. 변경 내용, 테스트 결과, 주의사항을 작성합니다.
5. PR을 생성하고 팀원에게 검토를 요청합니다.
6. 확인 후 `main`에 병합합니다.

PR에는 최소한 **무엇을 구현했는지, 어떤 파일을 수정했는지, 실행 테스트를 했는지** 적어 주세요.

---

## 8. 반드시 지켜야 할 충돌 방지 규칙

### 8-1. 공통 파일을 임의로 교체하지 않기

다음 파일은 여러 기능에 영향을 줍니다.

- `MainActivity.kt`
- `AppNavGraph.kt`
- `app/build.gradle.kts`
- 프로젝트 최상위 `build.gradle.kts`
- `settings.gradle.kts`
- `AndroidManifest.xml`
- `ui/theme/` 내부 파일
- 향후 생성될 공통 데이터 모델 및 저장 관련 파일

위 파일을 수정해야 한다면 **수정 전에 팀원에게 공유**해 주세요.

특히 AI가 제안한 코드를 적용할 때 공통 파일의 기존 내용을 통째로 덮어쓰지 않도록 주의합니다.

### 8-2. 서로 다른 담당자도 같은 기능을 중복 개발하지 않기

기능 간 연결이 필요한 경우 다음 사항을 먼저 합의합니다.

- 어떤 데이터가 필요한지
- 데이터를 누가 생성하고 관리하는지
- 다른 기능에서 어떻게 가져다 사용할지
- 데이터 타입과 이름을 무엇으로 통일할지
- 화면 이동이 어디에서 발생하는지
- 영구 저장이 필요한지

예시:

- A의 디톡스 달성 결과를 C의 마을 성장에 반영
- B의 걸음 수에 따른 보상을 C의 마을 성장에 반영
- D의 캐릭터 설정을 C의 화면에서 표시

이런 기능은 각 담당자가 데이터 구조를 임의로 확정하기 전에 협의해야 합니다.

### 8-3. 패키지명과 기존 폴더 구조 유지하기

기본 패키지명은 다음과 같습니다.

```kotlin
package com.team.digitaldetox
```

각 담당자는 본인 기능의 하위 패키지를 사용합니다.

예시:

```kotlin
package com.team.digitaldetox.feature.usage
```

패키지명, `namespace`, `applicationId`를 임의로 변경하지 마세요.

### 8-4. 라이브러리 추가 시 공유하기

새로운 외부 라이브러리가 필요하다면 다음 내용을 공유합니다.

- 라이브러리 이름과 사용 목적
- 필요한 Gradle 의존성
- 기존 의존성과 충돌 가능성
- Android 최소 지원 버전과의 호환성

기존 Gradle 파일을 전체 교체하지 말고 필요한 의존성만 추가합니다.

### 8-5. 충돌 발생 시

Git 충돌(Merge Conflict)이 발생하면 파일을 무작정 삭제하거나 다른 담당자의 변경 사항을 덮어쓰지 마세요.

충돌한 파일과 내용을 먼저 확인하고, 관련 담당자와 함께 수정합니다.

특히 `AppNavGraph.kt`, Gradle 설정, 공통 데이터 모델에서 충돌이 발생하면 함께 해결합니다.

---

## 9. 작업 시작 전·후 체크리스트

### 작업 시작 전

- `main`의 최신 변경 사항 확인
- 본인 담당 브랜치 확인
- 기존 코드와 파일 구조 확인
- 공통 파일 변경 필요 여부 확인
- 다른 담당자와 겹치는 기능이 없는지 확인

### 작업 완료 후

- 코드 컴파일 오류 확인
- 기능별 실행 테스트
- 기존 화면 이동이 정상인지 확인
- 불필요한 파일이 Git에 포함되지 않았는지 확인
- 담당 브랜치에 Commit 및 Push
- PR 생성
- 팀원 검토 후 `main`에 병합

### 다른 팀원의 작업이 병합된 이후

기능 브랜치에서 새 작업을 시작하기 전에 `main`의 최신 변경 사항을 확인하고, 필요할 경우 충돌 여부를 점검하면서 본인 브랜치에 반영합니다.

**브랜치 병합이나 Git 충돌 해결 방법이 익숙하지 않다면 임의로 `reset --hard`, `force push` 등을 실행하지 마세요.**

---

## 10. GitHub에 업로드하면 안 되는 파일

다음 항목은 GitHub에 올리지 않습니다.

```text
local.properties
.gradle/
.idea/
.kotlin/
**/build/
.DS_Store
```

또한 API Key, 비밀번호, 인증 토큰, 실제 사용자의 개인정보 등도 업로드하지 않습니다.

단, **Gradle Wrapper와 프로젝트 빌드에 필요한 설정 파일은 포함해야 합니다.**

예를 들어 다음 파일은 프로젝트를 공유할 때 필요합니다.

```text
gradlew
gradlew.bat
gradle/wrapper/
gradle/libs.versions.toml
build.gradle.kts
settings.gradle.kts
app/build.gradle.kts
```

Git 업로드 전 `.gitignore`와 `git status`를 확인해 주세요.

---

## 11. Figma 및 디자인 적용 규칙

Figma:

https://www.figma.com/design/z0OEO4y0agzgWqZGFzm4Xz/

현재 Figma 디자인은 초기 시안으로, 일부 화면과 리소스가 추후 변경될 수 있습니다.

- 현재 앱의 일부 UI는 임시 디자인입니다.
- 최종 디자인 확정 전 다른 담당자의 화면을 임의로 대규모 변경하지 않습니다.
- 공통 색상, 폰트, 버튼, 패널은 향후 재사용 가능한 공통 컴포넌트로 관리하는 것을 권장합니다.
- 이미지 리소스는 `res/drawable/` 등에 관리합니다.
- 같은 이미지 파일을 담당 폴더마다 중복 추가하지 않도록 주의합니다.
- 이미지 파일 이름은 Android 리소스 규칙에 맞게 영문 소문자와 밑줄 등을 사용합니다.

최종 디자인 적용 시 공통 UI 변경 사항은 팀원들과 공유합니다.

---

## 12. ChatGPT 및 AI 활용 시 필수 지침

팀원들은 ChatGPT 등 AI를 활용하여 개발할 수 있습니다.

다만 AI는 현재 GitHub 프로젝트의 모든 파일을 자동으로 알고 있지 않으므로, 작업할 때 README와 관련 Kotlin 코드, 필요하면 프로젝트 전체 파일을 제공해야 합니다.

### AI에 전달할 공통 프롬프트

```text
저희는 Android Studio, Kotlin, Jetpack Compose를 이용하여
OFF:ON 디지털 디톡스 앱을 개발하고 있습니다.

4명이 하나의 Android 프로젝트를 공유하며
담당 기능을 각자 다른 Git 브랜치에서 개발합니다.

기본 패키지는 com.team.digitaldetox 입니다.

첨부한 README와 기존 코드를 먼저 분석해 주세요.

저는 [담당 A/B/C/D]입니다.

반드시 다음 규칙을 지켜 주세요.

1. 현재 프로젝트 구조와 패키지명을 유지합니다.
2. 담당 기능을 중심으로 수정합니다.
3. 기존 MainActivity 및 AppNavGraph 연결을 보존합니다.
4. 다른 담당자의 코드를 삭제하거나 통째로 교체하지 않습니다.
5. 공통 파일 수정이 필요하면 수정 이유와 영향 범위를 먼저 설명합니다.
6. 현재 프로젝트의 Kotlin 및 Jetpack Compose 환경을 기준으로 작성합니다.
7. 코드를 수정할 때 정확한 파일 경로와 적용 방법을 알려줍니다.
8. 필요한 라이브러리와 Gradle 변경 사항을 별도로 안내합니다.
9. 기존 코드에서 구현된 기능과 미구현 기능을 구분합니다.
10. 컴파일 및 실행 여부를 직접 검증하지 않았다면 검증했다고 단정하지 않습니다.

먼저 현재 프로젝트 구조와 제 담당 영역을 분석하고,
기존 기능과 충돌하지 않는 개발 방향을 제안해 주세요.
```

### AI 사용 시 특히 주의할 점

- AI가 생성한 코드를 검증 없이 바로 Commit하지 않습니다.
- AI가 이전 버전의 코드를 기준으로 답변할 수 있으므로 최신 코드를 함께 전달합니다.
- 공통 파일 전체를 수정하라는 답변을 받으면 필요한 변경 사항인지 먼저 확인합니다.
- 개인별로 같은 기능을 다른 이름과 구조로 구현하지 않습니다.
- AI가 제안한 새 라이브러리나 데이터 모델은 팀 내부에서 공유합니다.

**README는 개발 기준 문서이며, 실제 코드의 최신 상태를 대신하지 않습니다.**

---

## 13. 데이터 및 권한 관련 주의사항

OFF:ON은 향후 스마트폰 사용 기록과 활동 정보를 활용할 예정입니다.

이러한 기능은 Android 기기 권한과 OS 버전의 영향을 받을 수 있으므로 다음 사항을 확인해야 합니다.

- 사용 기록 수집에 필요한 Android 시스템 권한
- 걸음 수 측정에 필요한 센서 및 권한
- 앱 차단 기능의 Android 정책 및 구현 가능 범위
- 에뮬레이터와 실제 스마트폰의 동작 차이
- 사용자 데이터 저장 및 개인정보 보호

특히 **앱 사용 시간 조회와 실제 앱 차단은 서로 다른 기능**입니다. 사용 기록을 수집할 수 있다고 해서 다른 앱을 자동으로 차단할 수 있는 것은 아닙니다.

구현 가능 여부와 권한 요구사항은 실제 기기에서 별도 테스트해야 합니다.

---

## 14. Git 관련 용어 정리

| 용어 | 의미 |
|---|---|
| Repository (Repo) | 프로젝트 코드와 변경 기록을 보관하는 저장소 |
| Clone | GitHub 프로젝트를 내 컴퓨터로 복사 |
| Branch | 다른 작업과 분리된 개발 공간 |
| Commit | 작업 변경 사항을 하나의 기록으로 저장 |
| Push | 로컬 Commit을 GitHub에 업로드 |
| Pull | GitHub의 변경 사항을 내 컴퓨터로 가져오기 |
| Pull Request (PR) | 작업한 브랜치를 다른 브랜치에 병합해 달라는 요청 |
| Merge | 서로 다른 브랜치의 변경 사항을 합치기 |
| Merge Conflict | 서로 다른 변경 사항이 충돌해 수동 해결이 필요한 상태 |

---

## 15. 협업 시 가장 중요한 원칙

1. **프로젝트는 하나만 사용합니다.** 각자 새 Android 프로젝트를 만들어 합치지 않습니다.
2. **담당 영역을 중심으로 개발합니다.** 다른 담당자의 코드를 임의로 변경하지 않습니다.
3. **공통 파일 변경은 먼저 공유합니다.** 특히 Navigation, Gradle, Manifest, 데이터 모델을 주의합니다.
4. **기능 개발은 브랜치에서 진행합니다.** `main`에는 PR로 병합합니다.
5. **UI 구현과 실제 기능 구현을 구분합니다.** 미구현 기능을 완료된 것으로 기록하지 않습니다.
6. **AI가 만든 코드는 직접 테스트합니다.** 기존 코드와 호환되는지 확인합니다.
7. **최신 `main`을 기준으로 협업합니다.** 새로운 작업을 시작하기 전에 변경 사항을 확인합니다.

---

## 프로젝트 링크

**GitHub Repository**

https://github.com/JaeeunChong/Digital_Detox_App

**Figma**

https://www.figma.com/design/z0OEO4y0agzgWqZGFzm4Xz/

본 README는 팀 공통 개발 기준입니다. 프로젝트 구조, 개발 규칙, 구현 상태가 변경될 경우 함께 갱신해 주세요.
