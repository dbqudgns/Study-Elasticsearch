# Elasticsearch 
학습 기간 : 2025/05/25 ~ 2025/06/

---

<details> <summary><strong> Elasticsearch 소개 </strong></summary>


**정의 : 검색, 데이터 분석에 최적화된 데이터베이스** 

**활용 사례 :** 

1. 데이터 수집 및 분석 : 대규모 데이터(ex. 로그 등)을 수집 및 분석하는데 최적화 할 수 있는 기능을 지님 
2. 검색 최적화 : 데이터가 많더라도 뛰어난 검색 속도를 가지고 있고, 오타나 동의어를 고려해서 유연하게 검색할 수 있는 기능을 지님 

**통신 방식 :** 

Elasticsearch와 소통하려면 **REST API** 방식으로 통신을 진행한다. 

ex)

```jsx
curl -X POST "localhost:9200/users/_doc" -H 'Content-Type: application/json' -d'
{
  "name": "Alice",
  "email": "alice@example.com"
}'
```
</details>

<details> <summary><strong> Elasticsearch의 기본 용어 정리 </strong></summary>

**Elasticsearch도 데이터베이스다 !!**

Elasticsearch에 데이터를 **저장/조회/수정/삭제**할 수 있다. 

데이터를 저장하기 위해 가장 먼저 **인덱스(index)**를 만든다. 

인덱스를 만들 때 어떤 유형의 데이터를 넣을 지 **매핑(mapping)**을 정의한다. 

테이블의 **필드(field)**에 맞게 데이터를 저장한다. 

| MySQL | Elasticsearch |
| --- | --- |
| 테이블(table) | 인덱스(index) |
| 컬럼(column) | 필드(field) |
| 레코드(record), 로우(row) | 도큐먼트(document) |
| 스키마(schema) | 매핑(mapping) |

</details>

<details> <summary><strong> Elasticsearch 인덱스/매핑/도큐먼트 CRUD </strong></summary>

1. **인덱스 생성, 조회 및 삭제** 

```bash
# 인덱스 생성 : PUT /인덱스명
PUT /users

# 인덱스 조회 : GET /인덱스명
GET /users

# 인덱스 삭제 : DELETE /인덱스명
DELETE /users
```

2. **매핑 정의** 

```bash
# 매핑 정의 :
# PUT /인덱스명/_mappings 
# {
#   "properties": {
#     "필드명": { "type": "타입" },
#     "필드명": { "type": "타입" },
#     "필드명": { "type": "타입" }
#   }
# }
PUT /users/_mappings
{
    "properties" : {
        "name" : { "type" : "keyword" },
        "age" : { "type" : "integer" },
        "is_active" : { "type" : "boolean" }
    }
}

```

타입 keyword는 문자열 string과 비슷한 성질을 가진다. 

- 인덱스 생성 + 매핑 정의

```bash
# PUT /인덱스명 
# {
#  "mappings" : {
#     "properties" : {
#       "필드명" : { "type" : "타입" }
PUT /products 
{ 
  "mappings" : {
    "properties" : {
       "name" : { "type" : "text" }
     }
   }
}
```

3. **도큐먼트 저장**
- id를 자동으로 생성해서 저장

```bash
# 도큐먼트 저장 (id 자동 생성) : 
# POST /인덱스명/_doc
# {
#  "필드명": "타입에 일치한 값", 
#  "필드명": "타입에 일치한 값",
#  "필드명": "타입에 일치한 값"
# }
POST /users/_doc
{
  "name": "dbqudgns",
  "age": 25,
  "is_active": true
}
```

- id를 직접 지정해서 저장

```bash
# 도큐먼트 저장 (id 직접 지정) : id가 이미 존재한다면 에러
# POST /인덱스명/_create/id값 
# {
#  "필드명": "타입에 일치한 값", 
#  "필드명": "타입에 일치한 값",
#  "필드명": "타입에 일치한 값"
# }
POST /인덱스명/_create/1 
{ 
  "name" : "dbqudgns",
  "age" : 25,
  "is_active" : true
```

- id를 직접 저장해서 저장
    - 이미 id가 존재한다면 도큐먼트를 덮어씌움 == UPSERT

```bash
# 도큐먼트 저장 및 업데이트
# PUT /인덱스명/_doc/id값
# {
#  "필드명": "타입에 일치한 값", 
#  "필드명": "타입에 일치한 값",
#  "필드명": "타입에 일치한 값"
# }
PUT /users/_doc/2
{
	"name": "jason",
  "age": 30,
  "is_active": true
}
```

4. **도큐먼트 조회** 
- 모든 도큐먼트 조회

```bash
# GET /인덱스명/_search
GET /users/_search
```

- id로 특정 도큐먼트 조회

```bash
# GET /인덱스명/_doc/id값
GET /users/_doc/1
GET /users/_doc/2
```

5. **도큐먼트 수정** 
- 도큐먼트를 통째로 덮어 씌우기

```bash
# PUT /인덱스명/_doc/id값 == 도큐먼트 저장 시에도 사용
# {
#  "필드명": "타입에 일치한 값", 
#  "필드명": "타입에 일치한 값",
#  "필드명": "타입에 일치한 값"
# }
PUT /users/_doc/1
{
	"name" : "new"
}
```

- 일부 필드만 수정

```bash
# POST /인덱스명/_update/id값
# {
#   "doc" : {
#     "일부 필드명" : "타입에 일치한 값",
#     "일부 필드명" : "타입에 일치한 값"
#    }
# }
PUT /users/_update/1
{ 
   "doc" : {
     "age" : 10,
     "is_active" : false
    }
}
```

6. **도큐먼트 삭제**

```bash
# id로 도큐먼트 삭제 : DELETE /인덱스명/_doc/id값
DELETE /users/_doc/2
```

</details>

<details> <summary><strong> 역 인덱스 </strong></summary>

필드 값을 단어마다 쪼개서 찾기 쉽게 정리해 놓은 목록이다. 

이때, 필드 값에서 추출돼 역 인덱스에 저장된 단어를 토큰이라 한다. 

- ex : “Apple 2024 아이패드”를 검색한 경우

| **토큰(token)** | **도큐먼트 id** |
| --- | --- |
| **Apple** | **[1, 2, 3]** |
| 2025 | [1] |
| 맥북 | [1] |
| 에어 | [1] |
| 13 | [1] |
| M4 | [1] |
| 10코어 | [1] |
| **2024** | **[2, 3]** |
| 에어팟 | [2] |
| 4세대 | [2] |
| **아이패드** | **[3]** |
| mini | [3] |
| A17 | [3] |
| Pro | [3] |

Elasticsearch의 자체적인 로직으로 score(점수)를 매겨 score가 높은 순으로 도큐먼트를 조회한다. 

- 위 표에서는 도큐먼트(2) → 도큐먼트(3) → 도큐먼트(1) 순으로 조회된다.

보통 검색어와 관련성이 높을 수록 도큐먼트의 score(점수)가 높게 측정된다. 

따라서, Elasticsearch가 역 인덱스의 기능을 갖고 있기 때문에,

단어의 순서랑 상관없이 도큐먼트를 조회할 수 있다 !!

</details>

<details> <summary><strong> 애널라이저(Analyzer) 및 다양한 검색 방법 </strong></summary>

정의 : 문자열(타입:text)을 토큰으로 변환시켜주는 장치 

- 위에서 언급한 단어 단위로 잘라 토큰을 만드는 것이 아닌 애널라이저가 여러 가지의 작업을 거쳐서 토큰을 만들어낸다.
- 애널라이저는 내부적으로 캐릭터 필터, 토크나이저, 토큰 필터를 활용해 문자열을 토큰으로 변환시킨다.

1. 캐릭터 필터(character filter)
- 문자열을 토큰으로 자르기 전에 문자열을 다듬는 역할
- 다양한 종류의 필터가 존재하며 여러 개의 필터를 적용시킬 수 있다.

ex) html_strip 필터 :  HTML 태그를 제거 

2. 토크나이저(tokenizer) 
- 문자열을 토큰으로 자르는 역할
- 한 개의 토크나이저만 사용 가능하다.

ex) standard 토크나이저 : 공백 또는 . , ! ? 와 같은 문장 부호를 기준으로 자름 

Hello, my name is Byeong-Hun

⇒ [ Hello, my, name, is, Byeong, Hun ]

3. 토큰 필터(token filter)
- 잘린 토큰을 최종적으로 다듬는 역할
- 다양한 종류의 필터가 존재하며, 여러 개의 필터를 적용시킬 수 있다.

ex) lowercase 필터 : 소문자로 변환

Hello, my name is Byeong-Hun

⇒ [ hello, my, name, is, byeong, hun ]

Elasticsearch에 기본 값으로 설정되어 있는 애널라이저는 ? ⇒ Standard Analyzer 

- character filter는 설정되어 있지 않다
- tokenizer는 standard로 설정되어 있다.
- token filter는 lowercase로 설정되어 있다.

Standard Analyzer 표현 방식 

```json
// 방법 1
"analyzer" : "standard"

// 방법 2 
"char_filter" : [],
"tokenizer" : "standard",
"filter" : ["lowercase"]
```

Analyze API : text 타입 문자열을 어떤식으로 토큰화하였는지 알려주는 API 

```json
// 방법 1
GET /_analyze
{
	"text" : "_____",
	"analyzer" : "standard"
}

// 방법 2
GET /_analyze
{
	"text" : "_____",
	"char_filter" : [],
	"tokenizer" : "standard",
	"filter" : ["lowercase"]
}	
```

특정 인덱스의 필드에 적용된 Analyzer을 기반으로 text 타입 문자열을 어떤식으로 토큰화하였는지 알려주는 API  

```json
GET /인덱스명/_analyze
{
  "field" : "필드명",
  "text" : "_____"
}
```

Custom Analyzer : 인덱스 생성 및 매핑 정의를 다같이 적용

```json
PUT /인덱스명
{
	"settings" : {
		"analysis" : {
		   "analyzer" : {
		     "커스텀 analyzer명" : {
				     "char_filter" : [],
				     "tokenizer" : "standard",
				     "filter" : []
				     }
				  }
			}
	},
	"mappings" : {
	   "properties" : {
	      "필드명" : {
	         "type" : "text",
	         "analyzer" : "커스텀 analyzer명" 
	         }
	      }
	  }
}          
```

### lowercase Token Filter

대소문자를 구분하지 않고 검색할 수 있게 만들기 위한 token filter 

만약, “Apple 2025 맥북 에어” 도큐먼트를 삽입하고 “Apple”을 검색하였다면?

- Analyze API를 통해 “Apple”은 “apple”로 토큰이 저장되어 있다.
- 하지만, “Apple”로 검색하면 왜 해당 도큐먼트가 조회가 되는 것일까 ?
    - 검색을 할 때도 Analyzer가 검색어로 입력한 문자열을 토큰으로 분리해 검색한다.
    - 따라서, “Apple”이라고 검색어를 입력하더라도 lowercase token filter에 의해 “apple”로 바뀐 채로 검색을 하게 된다.        

결론적으로 Analyzer에서 lowercase token filter를 활용함으로써 대소문자에 상관없이 데이터를 검색할 수 있게 된 것이다. 

### html_strip Character Filter

html_strip : HTML 태그를 제거해주는 character filter 

- character filter에서 불필요한 HTML을 제거해줘 검색 속도가 향상되고 역인덱스에 불필요한 토큰을 저장하지 않아 디스크 공간을 낭비하지 않게 된다.

### stop Token Filter

stop : 검색할 때 필요 없는 불용어(a, an, the, or, but 등) 제거해주는 필터 

- 불용어를 활용해 검색할 일이 없는 데이터라면 ? ⇒ stop을 활용
- 불용어를 포함해서 검색하는게 중요한 데이터라면 ? ⇒ stop 적용 X

### stemmer Token Filter

stemmer : 단어의 형태(-ed, -ing, -s, -er 등)에 상관없이 검색해주는 필터 

- stemmer는 영단어를 기본형으로 변환한 후에 토큰으로 저장한다.

### synonym Token Filter

synonym : 동의어로 검색하게 해주는 필터 

- 상황 예시 : “Samsung Notebook”이라는 상품이 있다고 가정하자 사용자는 검색할 때 “노트북”, “랩탑”, 휴대용 컴퓨터”, “laptop”과 같이 동의어로 검색할 수 있다.

Custom Analyzer를 활용해 인덱스 생성

```json
PUT /인덱스명
{
	"settings" : {
		"analysis" : {
		   "filter" : {
		     "커스텀 filter명" : {
		       "type" : "synonym",
		       "synonyms" : [
		          "동의어1", "동의어2", "동의어3", ~
		       ]
		    }
		 },
		 "analyzer" : {
		     "커스텀 analyzer명" : {
		        "char_filter" : [],
		        "tokenizer" : "standard",
		        "filter" : [
		           "lowercase",
		           "커스텀 filter명"
		        ]
		      }
		   }
		}
 },
 "mappings" : {
	 "properties" : {
	    "필드명" : {
	       "type" : "text",
	       "analyzer" : "커스텀 analyzer명"
	     }
	   }
	 }
}
```

### Nori Analyzer

한글 문장을 토큰으로 분해하여 역인덱스에 저장해주는 애널라이저 

Analyze API 활용해 nori tokenizer 기본 구성 파악하기

```json
// 방법 1
GET /_analyze
{
  "text" : "한글 문장",
  "analyzer" : "nori"
}

// 방법 2
GET /_analyze
{ 
  "text" : "한글 문장",
  "char_filter" : [],
  "tokenizer" : "nori_tokenizer",
  "filter" : ["nori_part_of_speech", "nori_readingform", "lowercase"]
}
```

- nori_part_of_speech : 의미 없는 조사(을, 의 등), 접속사 제거
- nori_readingform : 한자를 한글로 바꿔서 토큰으로 저장

### 한글과 영어가 섞인 문장 검색

Analyze API를 활용해 한글과 영어가 섞인 문장을 토큰화 파악하기

```json
GET /_analyze
{
  "text" : "한글과 영어가 mixed된 문장"
  "char_filter" : [],
  "tokenizer" : "nori_tokenizer",
  "filter" : ["nori_part_of_speech", "nori_readingform", "lowercase" "stop", "stemmer"] 
```

- 한글과 영어가 섞여있는 글이라면 Nori Analyzer를 활용하면 된다.
- 필드 값의 특징에 따라 char_filter 또는 token filter를 추가해서 조치를 취하면 된다.

</details>

<details> <summary><strong> 데이터 타입 </strong></summary>

자주 사용하는 데이터 타입에 대해 알아보자 

**[숫자]** 

- 10억 이하의 정수만 저장하면 되는 경우 : integer
- 10억이 넘어가는 정수를 저장해야 될 수도 있는 경우 : long
- 실수(소숫점을 가진 숫자 포함)를 저장해야 될 수도 있는 경우 : double

**[문자]**

- 문자열을 토큰으로 쪼개서 저장하고 싶은 경우 : text
    - 완전히 일치하지 않아도 비슷한 데이터를 조회해올 때 사용
- 문자열을 토큰으로 쪼개지 않고, 문자열 그대로 저장하고 싶은 경우 : keyword
    - 정확하게 일치할 때만 데이터를 조회해올 때 사용
    - ex) 휴대폰 번호, 이메일, 주민번호
- 계산에서 쓰는 값이 아니라면 숫자가 아닌 문자로 저장해야 한다 !!

**[기타]**

- 날짜 데이터를 저장해야 하는 경우 : date
- true, false를 저장해야 하는 경우 : boolean

Elasticsearch는 매핑을 정의하더라도 해당 필드가 반드시 존재하거나 null이면 안 된다는 제약을 두지 않는다. 

```json
POST /인덱스명/_doc 
{
  "필드명" : null,
  "content" : "필드 값"
}
```

Elasticsearch는 별도의 설정 없이 배열 형태의 데이터를 삽입할 수 있다. 

배열에 특화된 매핑 타입이 따로 없다 ! 

**별도 설정 없이 배열 형태로 값을 저장하고 각 값을 인덱싱한다.** 

```json
POST /인덱스명/_doc
{
  "필드명" : ["필드 값1", "필드 값2"]
}
```

</details>

<details> <summary><strong> 자주 사용하는 검색 기능 </strong></summary>

### 검색 키워드가 포함된 데이터를 조회하고 싶을 때 (match)

match 쿼리는 검색 키워드가 포함된 모든 도큐먼트를 조회한다. 

match 쿼리는 text 타입의 필드에서만 사용하는 쿼리이다. 

```json
# GET /인덱스명/_search
# {
#	 "query" : {
#       "match" : {
#           "특정 필드명" : "필드 값"
#       }
#    }
# }

GET /boards/_search
{
	 "query" : {
        "match" : {
            "categories" : "편의점 후기"
        }
    }
}
```

### 특정 값과 정확하게 일치하는 데이터를 조회하고 싶을 때 (term, terms)

term 쿼리는 특정 값과 정확히 일치하는 모든 도큐먼트를 조회한다.

term 쿼리는 text를 제외한 모든 타입에서 사용한다. 

- 특정 값과 정확하게 일치하는 데이터를 조회하고 싶을 때 : term
    - 데이터가 가진 값과 정확히 일치하게 검색하지 않으면 조회되지 않음

```json
# GET /인덱스명/_search
# {
#  "query" : {
#	   "term" : {
#	  	  "특정 필드명" : "필드 값"
#		   }
#	  }
# }

GET /boards/_search 
{
	"query" : {
		"term" : {
			"category" : "자유 게시판"
		}
	}
}
```

- 여러 개의 값 중 하나라도 일치하는 도큐먼트 조회 : terms
    - terms 쿼리는 여러 개의 값 중 하나라도 일치하는 모든 도큐먼트를 조회한다.

```json
# GET /인덱스명/_search
# {
#  "query" : {
#    "terms" : {
#      "특정 필드명" : ["필드 값1", "필드 값2", ... ]
#        }
#     }
# }

GET /boards/_search
{
  "query" : {
    "terms" : {
      "category" : ["자유 게시판", "익명 게시판"]
      }
   }
}
// category의 값이 "자유 게시판" 또는 "익명 게시판"인 데이터를 조회
```

### 2가지 이상의 조건을 만족시키는 데이터를 조회하고 싶을 때 (bool : filter, must)

2가지 이상의 조건을 만족시키는 쿼리를 작성하려면 bool 쿼리를 활용해야 한다. 

즉, bool 쿼리는 여러 쿼리를 조합하기 위해서 사용하는 개념이다. 

bool 쿼리의 must와 filter의 기능 :

- must : SQL문에서의 AND 역할을 수행
- filter : SQL문에서의 AND 역할을 수행

1. **must** 

```json
GET /인덱스명/_search
{
	"query" : {
		"bool" : {
			"must" : [
				{
				 "match" : {
						"필드명" : "필드 값" 
			  	}
				},
				{
				 "match" : {
					 "필드명" : "필드 값" 		
					}
				}
			]
		}
	}
}
```

- **must는 score(점수)에 영향을 받는 쿼리이다.**
    - 따라서, match 쿼리와 함께 쓰인다.
    - 유연한 검색이 필요할 때 쓰인다.
    - 즉, 정확하게 일치하지 않더라도 관련성이 있는 데이터까지 조회하는 쿼리에서 사용된다.

1. **filter**

```json
GET /인덱스명/_search
{
	"query" : {
		"bool" : {
			"filter" : [
				{
				 "term" : {
						"필드명" : "필드 값" 
			  	}
				},
				{
				 "term" : {
					 "필드명" : "필드 값" 		
					}
				}
			]
		}
	}
}
```

- **filter는 score(점수)에 영향을 받지 않는 쿼리이다.**
    - 따라서, term 쿼리와 함께 쓰인다.
    - 정확한 검색이 필요할 때 쓰인다.
    - 즉, 검색어와 일치하지 않는 경우 해당 데이터를 조회하지 않는다.

- **must와 filter 혼합해서 사용해보기**

ex) 자유 게시판의 게시글 중에서 검색엔진과 관련된 글을 찾고 싶다. 그런데 공지글이 아닌 게시글 중에서만 검색하고 싶다. 

```json
GET /boards/_search 
{
	"query" : {
		"bool" : {
			"must" : [
				{ "match" : { "title" : "검색엔진" } } // 유연한 검색이 필요 (must)
				],
				"filter" : [
					{ "term" : { "category" : "자유 게시판" } }, // 정확한 검색이 필요 (filter)
					{ "term" : { "is_notice" : false } } // 정확한 검색이 필요 (filter) 
				]
			}
	 }
}
```

### 특정 조건을 만족하지 않는 데이터를 조회하고 싶을 때 (bool : must_not)

bool 쿼리 중 must_not는 SQL 문에서 NOT 역할을 수행한다. 

ex) 광고 게시판의 글이 아니면서, 공지 글이 아니면서, 검색엔진의 키워드와 관련된 게시글을 조회하고 싶다.

```json
GET /boards/_search 
{
	"query" : {
		"bool" : {
			"must" : [
				{ "match" : { "title" : "검색엔진" } } // 유연한 검색이 필요 (must)
				],
				"must_not" : [
					{ "term" : { "category" : "광고 게시판" } }, // 광고 게시판 X
					{ "term" : { "is_notice" : true } } // 공지 글 X
				]
			}
	 }
}
```

- must_not는 term, match 두 개 다 사용이 가능하다 !

### 숫자/날짜의 값에 대해 범위 조건으로 데이터를 조회하고 싶을 때 (range)

범위를 지정하여 해당 조건에 맞는 데이터를 조회하고 싶을 때 range 쿼리를 사용한다. 

나이가 30살이면서 회원가입 날짜가 2025년 1월 1일 이하인 사용자를 조회해보자

```json
GET /users/_search
{
	"query" : {
		"bool" : {
			"filter" : 
			[
				{ "range" : {	"age" : { "gte" : 30 } } },
				{ "range" : { "created_at" : { "lte" : "2025-01-01" } } } 
			]
}
```

- range로 범위를 설정할 시 조건에 맞는 도큐먼트를 조회해야 하므로 filter 쿼리를 사용한다.

range 쿼리에서 사용하는 연산자

![image](https://github.com/user-attachments/assets/979026a0-da85-492f-8977-1f11f01a613d)


### 특정 조건을 만족하는 데이터 위주로 상위 노출 시키고 싶을 때(should)

bool 쿼리에서 must와 filter는 반드시 조건을 만족하는 데이터만 조회되지만, 

should는 조건을 만족하지 않는 데이터도 조회되기도 한다. 

- should의 조건을 충족시키는 데이터일 경우 score(점수)에 가산점을 부여여해 상위 노출될 가능성이 높아진다.
- must, filter는 **“무조건”**의 느낌이지만 should는 **“있으면 좋고, 아니면 말고”**의 느낌이다.

[사례]

사용자가 “백엔드 개발자”를 검색했을 때, 우리는 다음 조건을 만족하는 공고를 더 상위에 노출시키고 싶은 경우 

- 원격 근무
- 연봉 6,000만원 이상
- 유명 기업(ex, 네카라쿠배)

위 조건은 필수가 아니고, 만족하면 더 매력적인 공고이므로 should로 가산점을 부여해 상위 노출되게 만들 수 있다. 

 사용자가 “무선 이어폰”로 검색했을 때, 평점이 4.5 이상이거나 좋아요 수가 100개 이상인 게시글을 상위 노출시키고 싶을 때  

```json
GET /products/_search
{
	"query" : {
		"bool" : {
			"must" : [ 
				{ "match" : { "name" : "무선 이어폰" } }
			],
			"should" : [
				{ "range" : { "rating" : { "gte" : 4.5 } } }, 
				// 4.5 이상의 평점의 상품일 경우 score에 가산점 부여
				{ "range" : { "likes" : { "gte" : 100 } } }
				// 좋아요 수가 100개 이상인 상품일 경우 score에 가산점 부여
			]
	 }
}
```

만약, must 또는 filter가 없고 should만 있을 경우 

```json
GET /products/_search
{
  "query" : {
    "bool" : {
      "should" : [
        { "match" : { "title" : "엘라스틱서치 적용 후기" } },
        { "match" : { "content" : "엘라스틱서치 적용 후기" } }
      ]
    }
  }
}
```

- 최소 하나의 should 조건은 반드시 만족해야 해당 문서가 검색된다.

### 오타가 있더라도 유사한 단어를 포함한 데이터를 조회하고 싶을 때 (fuzziness)

검색 도중에 의도한 단어로 검색하지 않고 오타가 있는 단어로 검색하는 경우가 있다. 

그런데, 결과는 내가 의도한 검색 결과가 나온다. 

이때, 사용되는 것이 fuzziness이다. 

 

```json
GET /인덱스명/_search
{
	"query" : {
		"match" : {
			"필드명" : {
				"query" : "오타 검색",
				"fuzziness" : "AUTO"
			}
		}
	}
}				
```

- fuzziness : AUTO : 단어 길이에 따라 오타 허용 개수를 자동으로 설정

ex) 단어 길이가 8개이면 3개까지 오타를 허용

### 여러 필드에서 검색 키워드가 포함된 데이터를 조회하고 싶을 때 (multi_match)

특정 키워드로 검색해보면 사이트의 제목(title) 뿐만 아니라 내용(content)을 포함해서 검색한다. 

이때, 사용되는 것이 multi_match이다. 

```json
GET /인덱스명/_search
{
	"query" : {
		"multi_match" : {
			"query" : "검색 키워드",
			"fields" : ["필드1", "필드2"]
		}
	}
}

GET /boards/_search
{
  "query": {
    "multi_match": {
      "query": "엘라스틱서치 적용 후기",
      "fields": ["title", "content"]
    }
  }
}
```

- title 또는 content 필드에 검색 키워드가 포함된 데이터가 조회된다.
    - title이랑 content 둘 다에 키워드가 포함되지 않은 데이터는 제외된다.

가중치 활용해서 검색하기

- content에만 키워드가 포함된 글보다 title에만 키워드가 포함된 글을 더 상위노출 시키고 싶을 수 있다.

```json
GET /boards/_search
{
	"query" : {
		"multi_match" : {
			"query" : "엘라스틱서치 적용 후기"
			"fields" : ["title^2", "content"]
			}
	 }
}
```

- title에 2배 더 높은 score 점수를 부여

### 검색한 키워드를 하이라이팅 처리하고 싶을 때 (highlight)

구글 or 네이버에서 검색을 해보면 검색한 키워드가 하이라이팅 처리가 되있는 걸 확인할 수 있다. 

이때, highlight을 이용해 특정 필드에 HTML코드 처리를 할 수 있다. 

```json
GET /boards/_search
{
	"query" : {
		"multi_match" : {
			"query" : "엘라스틱서치 적용 후기",
			"fields" : ["title", "content"]
		}
	},
	"highlight" : {
		"fields" : {
			"title" : {
				"pre_tags" : ["<mark>"],
				"post_tags" : ["</mark"]
			},
			"content" : {
				"pre_tags" : ["<b>"],
				"post_tags" : ["</b>"]
			}
		}
	}
}
```

### 페이지네이션(size, from), 정렬(sort)

서버의 과부하를 방지하기 위해 데이터 조회 시 필수적으로 적용해야 하는 기능이 페이지네이션이다. 

```json
GET /boards/_search
{
    "query" : {
        "match" : {
            "title" : "글"
        }
    },
    "size" : 3,
    "from" : 0
}
```

- size : 한 페이지에 불러올 데이터 개수 (SQL문의 LIMIT과 동일)
- from : 몇 번째 데이터부터 불러올 지 (SQL문의 OFFSET과 동일, 0부터 시작)

좋아요 수(likes)를 내림차순으로 정렬해보자

```json
GET /boards/_search
{
	"query" : {
		"match" : {
			 "title" : "글"
		 }
	},		
	"sort" : [
			{ "likes" : { "order" : "desc" } }
	]
}
```

### 하나의 필드에 text와 keyword 타입을 동시에 사용하고 싶을 때 (Multi Field)

유연한 검색(text)과 정확한 검색(keyword) 둘 다 하고 싶을 수 있다. 

즉, 하나의 필드의 타입을 text와 keyword로 설정해야 한다. 

```json
PUT /인덱스명
{
    "mappings" : {
        "properties" : {
            "필드명" : {
                "type" : "text",
                "analyzer" : "nori",
                "fields" : {
                    "서브 필드명" : {
                        "type" : "keyword"
                     }
                 }
           }
     }
}

PUT /products
{
    "mappings" : {
        "properties" : {
            "category" : {
                "type" : "text",
                "analyzer" : "nori",
                "fields" : {
                    "raw" : {
                        "type" : "keyword"
                    }
                }
           }
     }
}
```

서브 필드명을 통해 검색하기 

```json
GET /인덱스명/_search
{
    "query" : {
        "term" : {
            "필드명.서브필드명" : "검색할 값"
        }
    }
}

GET /products/_search
{
    "query" : {
        "term" : {
            "category.raw" : "특수 가전제품"
        }
    }
}
```

### **검색 키워드를 일부 입력했을 때 검색어를 추천해주는 기능 (자동 완성 기능)**

Elasticsearch에서 자동  완성 기능을 구현하는 방법에는 정말 다양한 방법이 존재한다. 

그 중에서 가성비가 좋은 방법인 **search_as_you_type**을 알아보자 !

**search_as_you_type** 

- Elasticsearch에서 자동 완성 기능을 쉽게 구현할 수 있게 설계된 데이터 타입이다.
- text 타입처럼 애널라이저를 거쳐 토큰으로 분리된다.
- 위 타입을 활용해 필드를 만드면 필드 내부적으로 _2gram, _3gram이라는 멀티 필드(Multi Field)도 같이 만든다.
    - _2gram : 두 단어씩 묶어서 토큰을 만들어 인덱싱 된다.
    - _3gram : 세 단어씩 묶어서 토큰을 만들어 인덱싱 된다.

```json
PUT /인덱스명
{
    "mappings" : {
        "properties" : {
            "필드명" : {
                "type" : "search_as_you_type",
                "analyzer" : "애널라이저명"
            }
        }
    }
}

PUT /products
{
    "mappings" : {
        "properties" : {
            "name" : {
                "type" : "search_as_you_type",
                "analyzer" : "nori"
            }
        }
    }
}
```

자동 완성 데이터 검색하기 

```json
GET /인덱스명/_search
{
  "query": {
    "multi_match": {
      "query": "검색 키워드", 
      "type": "bool_prefix", 
      "fields": [
        "필드명",
        "필드명._2gram",
        "필드명._3gram"
      ]
    }
  }
}

GET /products/_search
{
  "query": {
    "multi_match": {
      "query": "돌김", 
      "type": "bool_prefix", // "돌김"이라는 검색 키워드와 일치하는 데이터를 매치 
      "fields": [
        "name",
        "name._2gram",
        "name._3gram" //"돌김" 단어가 위 3개의 필드 중 연속적으로 들어가 있으면 score가 높아져 상위 노출
      ]
    }
  }
}
```

- **multi_match** : 여러 필드에 걸쳐서 데이터 검색
- **bool_prefix** : 데이터 타입이 아닌 매치 타입
    - 단어 단위로 나누어 앞쪽 단어는 match, 마지막 단어는 prefix match
    - you have th라고 검색하면 앞쪽 단어인 you와 have는 역인덱스에 저장된 토큰과 일치하는  데이터를 찾고, 마지막 단어인 th는 역인덱스에 저장된 토큰 중에 th로 시작하는 데이터를 찾는다.
- **name._2gram, name._3gram** 필드에서도 검색을 하는 이유는 연속으로 단어가 일치할수록 score를 더 높게 측정해 상위 노출되도록 만들기 위함이다.
 
</details>
