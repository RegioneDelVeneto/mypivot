plugins {
  java
  //license header
  id("com.github.hierynomus.license") version "0.16.1"
}

sourceSets {
  main {
    resources {
      srcDir(".")
      include(
        "mypivot4-be/*",
        "mypivot4-be/src/**",
        "mypivot4-fe/*",
        "mypivot4-fe/projects/**",
        "db/**",
        //"mypivot4-batch/**", //as of now there is nothing to add
      )
      exclude(
        "**/.*/**",
        "mypivot4-be/src/main/resources/wsdl/mypivot/FlussoRiversamento_1_0_4.xsd",
        // fe
        "mypivot4-fe/node_modules/**",
        "mypivot4-fe/dist/**",
        "mypivot4-fe/.idea/**",
        "mypivot4-fe/version.ts",
        "mypivot4/projects/mypay4-fe-common/assets/cookiebar/**",
        // batch
      )
    }
  }
}

license {
  header = rootProject.file("LICENSE_HEADER")
  strictCheck = true
  mapping(mapOf(
    Pair("wsdl", "XML_STYLE"),
    Pair("xjb", "XML_STYLE"),
    Pair("jrxml", "XML_STYLE"),
    Pair("kts", "SLASHSTAR_STYLE"),
    Pair("ts", "SLASHSTAR_STYLE"),
    Pair("css", "JAVADOC_STYLE"),
    Pair("scss", "JAVADOC_STYLE"),
  ))
  includes(listOf(
    "**/*.kts",
    "**/*.java",
    "**/*.sql",
    "**/*.wsdl",
    "**/*.xsd",
    "**/*.xjb",
    "**/*.properties",
    "**/*.jrxml",
    "**/*.ts",
    "**/*.html",
    "**/*.ts",
    "**/*.scss",
  ))
  ext.set("year", "2022") //Calendar.getInstance().get(Calendar.YEAR))
  ext.set("name", "Regione Veneto")
  ext.set("desc", "MyPivot - Accounting reconciliation system of Regione Veneto.")
}