.class public Lorg/cert/sendsms/CheckActivityRule;
.super Ljava/lang/Object;
.source "CheckActivityRule.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    .prologue
    .line 25
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static back(Landroid/app/Activity;)V
    .locals 5
    .param p0, "act"    # Landroid/app/Activity;

    .prologue
    .line 144
    invoke-virtual {p0}, Landroid/app/Activity;->getApplicationContext()Landroid/content/Context;

    move-result-object v2

    const-string v3, "\u53d1\u751f\u8d8a\u6743\u8c03\u7528\uff0c\u5c06\u8fd4\u56de"

    const/4 v4, 0x1

    invoke-static {v2, v3, v4}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;

    move-result-object v2

    invoke-virtual {v2}, Landroid/widget/Toast;->show()V

    .line 145
    invoke-static {}, Ljava/lang/Runtime;->getRuntime()Ljava/lang/Runtime;

    move-result-object v1

    .line 147
    .local v1, "runtime":Ljava/lang/Runtime;
    :try_start_0
    const-string v2, "input keyevent 4"

    invoke-virtual {v1, v2}, Ljava/lang/Runtime;->exec(Ljava/lang/String;)Ljava/lang/Process;
    :try_end_0
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_0} :catch_0

    .line 151
    :goto_0
    return-void

    .line 148
    :catch_0
    move-exception v0

    .line 149
    .local v0, "e":Ljava/io/IOException;
    invoke-virtual {v0}, Ljava/io/IOException;->printStackTrace()V

    goto :goto_0
.end method

.method public static generateEntryPointPermissionMap(Ljava/io/InputStream;)Ljava/util/Map;
    .locals 8
    .param p0, "is"    # Ljava/io/InputStream;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/io/InputStream;",
            ")",
            "Ljava/util/Map",
            "<",
            "Ljava/lang/String;",
            "Ljava/util/Set",
            "<",
            "Ljava/lang/String;",
            ">;>;"
        }
    .end annotation

    .prologue
    .line 160
    new-instance v6, Ljava/util/HashMap;

    invoke-direct {v6}, Ljava/util/HashMap;-><init>()V

    .line 162
    .local v6, "sourceToExitMap":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Ljava/util/Set<Ljava/lang/String;>;>;"
    :try_start_0
    new-instance v5, Ljava/io/InputStreamReader;

    invoke-direct {v5, p0}, Ljava/io/InputStreamReader;-><init>(Ljava/io/InputStream;)V

    .line 163
    .local v5, "reader":Ljava/io/InputStreamReader;
    new-instance v0, Ljava/io/BufferedReader;

    invoke-direct {v0, v5}, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V

    .line 164
    .local v0, "bufferedReader":Ljava/io/BufferedReader;
    invoke-virtual {v0}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object v2

    .line 165
    .local v2, "line":Ljava/lang/String;
    :cond_0
    :goto_0
    if-eqz v2, :cond_2

    .line 167
    const-string v7, "<"

    invoke-virtual {v2, v7}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v7

    if-eqz v7, :cond_0

    .line 168
    move-object v3, v2

    .line 169
    .local v3, "method":Ljava/lang/String;
    new-instance v4, Ljava/util/HashSet;

    invoke-direct {v4}, Ljava/util/HashSet;-><init>()V

    .line 170
    .local v4, "permissions":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/String;>;"
    invoke-virtual {v0}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object v2

    .line 171
    :goto_1
    if-eqz v2, :cond_1

    const-string v7, "<"

    invoke-virtual {v2, v7}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v7

    if-nez v7, :cond_1

    .line 172
    invoke-interface {v4, v2}, Ljava/util/Set;->add(Ljava/lang/Object;)Z

    .line 173
    invoke-virtual {v0}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object v2

    goto :goto_1

    .line 175
    :cond_1
    invoke-interface {v6, v3, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_0
    .catch Ljava/io/FileNotFoundException; {:try_start_0 .. :try_end_0} :catch_0
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_0} :catch_1

    goto :goto_0

    .line 178
    .end local v0    # "bufferedReader":Ljava/io/BufferedReader;
    .end local v2    # "line":Ljava/lang/String;
    .end local v3    # "method":Ljava/lang/String;
    .end local v4    # "permissions":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/String;>;"
    .end local v5    # "reader":Ljava/io/InputStreamReader;
    :catch_0
    move-exception v1

    .line 179
    .local v1, "e":Ljava/io/FileNotFoundException;
    invoke-virtual {v1}, Ljava/io/FileNotFoundException;->printStackTrace()V

    .line 183
    .end local v1    # "e":Ljava/io/FileNotFoundException;
    :cond_2
    :goto_2
    return-object v6

    .line 180
    :catch_1
    move-exception v1

    .line 181
    .local v1, "e":Ljava/io/IOException;
    invoke-virtual {v1}, Ljava/io/IOException;->printStackTrace()V

    goto :goto_2
.end method

.method public static isPermissionRedelegation(IILandroid/content/Intent;Landroid/app/Activity;)Z
    .locals 18
    .param p0, "requestCode"    # I
    .param p1, "resultCode"    # I
    .param p2, "data"    # Landroid/content/Intent;
    .param p3, "act"    # Landroid/app/Activity;

    .prologue
    .line 38
    const/4 v2, 0x0

    .line 39
    .local v2, "callerPackage":Ljava/lang/String;
    invoke-virtual/range {p3 .. p3}, Landroid/app/Activity;->getIntent()Landroid/content/Intent;

    move-result-object v7

    .line 40
    .local v7, "intent":Landroid/content/Intent;
    const-string v16, "Intent"

    const-string v15, "package"

    invoke-virtual {v7, v15}, Landroid/content/Intent;->getStringExtra(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v15

    if-nez v15, :cond_3

    const-string v15, "nothing"

    :goto_0
    move-object/from16 v0, v16

    invoke-static {v0, v15}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 41
    if-nez p1, :cond_0

    if-nez p0, :cond_0

    if-eqz p2, :cond_0

    .line 42
    const-string v15, "package"

    move-object/from16 v0, p2

    invoke-virtual {v0, v15}, Landroid/content/Intent;->hasExtra(Ljava/lang/String;)Z

    move-result v15

    if-eqz v15, :cond_0

    .line 43
    invoke-virtual/range {p2 .. p2}, Landroid/content/Intent;->getExtras()Landroid/os/Bundle;

    move-result-object v15

    const-string v16, "package"

    invoke-virtual/range {v15 .. v16}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v15

    if-eqz v15, :cond_0

    .line 44
    invoke-virtual/range {p2 .. p2}, Landroid/content/Intent;->getExtras()Landroid/os/Bundle;

    move-result-object v15

    const-string v16, "package"

    invoke-virtual/range {v15 .. v16}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    .line 48
    :cond_0
    new-instance v4, Ljava/util/HashSet;

    invoke-direct {v4}, Ljava/util/HashSet;-><init>()V

    .line 49
    .local v4, "callerPermissionsSet":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/String;>;"
    new-instance v1, Ljava/util/HashSet;

    invoke-direct {v1}, Ljava/util/HashSet;-><init>()V

    .line 50
    .local v1, "calleePermissionSet":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/String;>;"
    invoke-virtual/range {p3 .. p3}, Landroid/app/Activity;->getPackageName()Ljava/lang/String;

    move-result-object v14

    .line 51
    .local v14, "selfPackage":Ljava/lang/String;
    if-eqz v2, :cond_7

    invoke-virtual {v2, v14}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v15

    if-nez v15, :cond_7

    .line 52
    invoke-static {v14, v2}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 54
    const/4 v13, 0x0

    .line 55
    .local v13, "packageInfo":Landroid/content/pm/PackageInfo;
    :try_start_0
    invoke-virtual/range {p3 .. p3}, Landroid/app/Activity;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v15

    invoke-virtual/range {p3 .. p3}, Landroid/app/Activity;->getPackageManager()Landroid/content/pm/PackageManager;

    const/16 v16, 0x1000

    move/from16 v0, v16

    invoke-virtual {v15, v2, v0}, Landroid/content/pm/PackageManager;->getPackageInfo(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;

    move-result-object v13

    .line 56
    iget-object v3, v13, Landroid/content/pm/PackageInfo;->requestedPermissions:[Ljava/lang/String;

    .line 57
    .local v3, "callerPermissions":[Ljava/lang/String;
    if-eqz v3, :cond_1

    .line 58
    invoke-static {v4, v3}, Ljava/util/Collections;->addAll(Ljava/util/Collection;[Ljava/lang/Object;)Z

    .line 60
    :cond_1
    invoke-virtual/range {p3 .. p3}, Landroid/app/Activity;->getComponentName()Landroid/content/ComponentName;

    move-result-object v15

    invoke-virtual {v15}, Landroid/content/ComponentName;->getClassName()Ljava/lang/String;

    move-result-object v5

    .line 61
    .local v5, "className":Ljava/lang/String;
    new-instance v15, Ljava/lang/Throwable;

    invoke-direct {v15}, Ljava/lang/Throwable;-><init>()V

    invoke-virtual {v15}, Ljava/lang/Throwable;->getStackTrace()[Ljava/lang/StackTraceElement;

    move-result-object v15

    const/16 v16, 0x1

    aget-object v15, v15, v16

    invoke-virtual {v15}, Ljava/lang/StackTraceElement;->getMethodName()Ljava/lang/String;

    move-result-object v11

    .line 63
    .local v11, "methodName":Ljava/lang/String;
    new-instance v15, Ljava/lang/StringBuilder;

    invoke-direct {v15}, Ljava/lang/StringBuilder;-><init>()V

    const-string v16, "<"

    invoke-virtual/range {v15 .. v16}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v15

    invoke-virtual {v15, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v15

    const-string v16, ": "

    invoke-virtual/range {v15 .. v16}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v15

    invoke-virtual {v15, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v15

    invoke-virtual {v15}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v12

    .line 64
    .local v12, "methodSignature":Ljava/lang/String;
    const-string v16, "Activity "

    new-instance v17, Ljava/lang/StringBuilder;

    invoke-direct/range {v17 .. v17}, Ljava/lang/StringBuilder;-><init>()V

    if-nez p3, :cond_4

    const/4 v15, 0x1

    :goto_1
    move-object/from16 v0, v17

    invoke-virtual {v0, v15}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v15

    const-string v17, ""

    move-object/from16 v0, v17

    invoke-virtual {v15, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v15

    invoke-virtual {v15}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v15

    move-object/from16 v0, v16

    invoke-static {v0, v15}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 65
    const-string v16, "Activity Resources"

    new-instance v17, Ljava/lang/StringBuilder;

    invoke-direct/range {v17 .. v17}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual/range {p3 .. p3}, Landroid/app/Activity;->getResources()Landroid/content/res/Resources;

    move-result-object v15

    if-nez v15, :cond_5

    const/4 v15, 0x1

    :goto_2
    move-object/from16 v0, v17

    invoke-virtual {v0, v15}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v15

    const-string v17, ""

    move-object/from16 v0, v17

    invoke-virtual {v15, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v15

    invoke-virtual {v15}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v15

    move-object/from16 v0, v16

    invoke-static {v0, v15}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 66
    const-string v15, "Activity entry"

    const-string v16, "2130968576"

    invoke-static/range {v15 .. v16}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 67
    invoke-virtual/range {p3 .. p3}, Landroid/app/Activity;->getResources()Landroid/content/res/Resources;

    move-result-object v15

    const/high16 v16, 0x7fff0000

    invoke-virtual/range {v15 .. v16}, Landroid/content/res/Resources;->openRawResource(I)Ljava/io/InputStream;

    move-result-object v8

    .line 68
    .local v8, "is":Ljava/io/InputStream;
    const-string v15, "2130968576"

    const-string v16, "2130968576"

    invoke-static/range {v15 .. v16}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 69
    invoke-static {v8}, Lorg/cert/sendsms/CheckActivityRule;->generateEntryPointPermissionMap(Ljava/io/InputStream;)Ljava/util/Map;

    move-result-object v10

    .line 70
    .local v10, "map":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Ljava/util/Set<Ljava/lang/String;>;>;"
    invoke-interface {v10}, Ljava/util/Map;->keySet()Ljava/util/Set;

    move-result-object v15

    invoke-interface {v15}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v16

    :cond_2
    :goto_3
    invoke-interface/range {v16 .. v16}, Ljava/util/Iterator;->hasNext()Z

    move-result v15

    if-eqz v15, :cond_6

    invoke-interface/range {v16 .. v16}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v9

    check-cast v9, Ljava/lang/String;

    .line 72
    .local v9, "key":Ljava/lang/String;
    invoke-virtual {v9, v5}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v15

    if-eqz v15, :cond_2

    invoke-virtual {v9, v11}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v15

    if-eqz v15, :cond_2

    .line 73
    invoke-interface {v10, v9}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    .line 74
    invoke-interface {v10, v9}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v15

    move-object v0, v15

    check-cast v0, Ljava/util/Set;

    move-object v1, v0
    :try_end_0
    .catch Landroid/content/pm/PackageManager$NameNotFoundException; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_3

    .line 40
    .end local v1    # "calleePermissionSet":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/String;>;"
    .end local v3    # "callerPermissions":[Ljava/lang/String;
    .end local v4    # "callerPermissionsSet":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/String;>;"
    .end local v5    # "className":Ljava/lang/String;
    .end local v8    # "is":Ljava/io/InputStream;
    .end local v9    # "key":Ljava/lang/String;
    .end local v10    # "map":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Ljava/util/Set<Ljava/lang/String;>;>;"
    .end local v11    # "methodName":Ljava/lang/String;
    .end local v12    # "methodSignature":Ljava/lang/String;
    .end local v13    # "packageInfo":Landroid/content/pm/PackageInfo;
    .end local v14    # "selfPackage":Ljava/lang/String;
    :cond_3
    const-string v15, "package"

    invoke-virtual {v7, v15}, Landroid/content/Intent;->getStringExtra(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v15

    goto/16 :goto_0

    .line 64
    .restart local v1    # "calleePermissionSet":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/String;>;"
    .restart local v3    # "callerPermissions":[Ljava/lang/String;
    .restart local v4    # "callerPermissionsSet":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/String;>;"
    .restart local v5    # "className":Ljava/lang/String;
    .restart local v11    # "methodName":Ljava/lang/String;
    .restart local v12    # "methodSignature":Ljava/lang/String;
    .restart local v13    # "packageInfo":Landroid/content/pm/PackageInfo;
    .restart local v14    # "selfPackage":Ljava/lang/String;
    :cond_4
    const/4 v15, 0x0

    goto/16 :goto_1

    .line 65
    :cond_5
    const/4 v15, 0x0

    goto :goto_2

    .line 77
    .restart local v8    # "is":Ljava/io/InputStream;
    .restart local v10    # "map":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Ljava/util/Set<Ljava/lang/String;>;>;"
    :cond_6
    :try_start_1
    invoke-interface {v1, v4}, Ljava/util/Set;->removeAll(Ljava/util/Collection;)Z

    .line 78
    invoke-interface {v1}, Ljava/util/Set;->size()I
    :try_end_1
    .catch Landroid/content/pm/PackageManager$NameNotFoundException; {:try_start_1 .. :try_end_1} :catch_0

    move-result v15

    if-lez v15, :cond_7

    .line 79
    const/4 v15, 0x1

    .line 85
    .end local v3    # "callerPermissions":[Ljava/lang/String;
    .end local v5    # "className":Ljava/lang/String;
    .end local v8    # "is":Ljava/io/InputStream;
    .end local v10    # "map":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Ljava/util/Set<Ljava/lang/String;>;>;"
    .end local v11    # "methodName":Ljava/lang/String;
    .end local v12    # "methodSignature":Ljava/lang/String;
    .end local v13    # "packageInfo":Landroid/content/pm/PackageInfo;
    :goto_4
    return v15

    .line 81
    .restart local v13    # "packageInfo":Landroid/content/pm/PackageInfo;
    :catch_0
    move-exception v6

    .line 82
    .local v6, "e":Landroid/content/pm/PackageManager$NameNotFoundException;
    invoke-virtual {v6}, Landroid/content/pm/PackageManager$NameNotFoundException;->printStackTrace()V

    .line 85
    .end local v6    # "e":Landroid/content/pm/PackageManager$NameNotFoundException;
    .end local v13    # "packageInfo":Landroid/content/pm/PackageInfo;
    :cond_7
    const/4 v15, 0x0

    goto :goto_4
.end method

.method public static isPermissionRedelegation(Landroid/app/Activity;)Z
    .locals 17
    .param p0, "act"    # Landroid/app/Activity;

    .prologue
    .line 96
    const/4 v2, 0x0

    .line 97
    .local v2, "callerPackage":Ljava/lang/String;
    invoke-virtual/range {p0 .. p0}, Landroid/app/Activity;->getIntent()Landroid/content/Intent;

    move-result-object v6

    .line 98
    .local v6, "data":Landroid/content/Intent;
    const-string v16, "Intent"

    const-string v15, "package"

    invoke-virtual {v6, v15}, Landroid/content/Intent;->getStringExtra(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v15

    if-nez v15, :cond_3

    const-string v15, "nothing"

    :goto_0
    move-object/from16 v0, v16

    invoke-static {v0, v15}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 99
    if-eqz v6, :cond_0

    .line 100
    const-string v15, "package"

    invoke-virtual {v6, v15}, Landroid/content/Intent;->hasExtra(Ljava/lang/String;)Z

    move-result v15

    if-eqz v15, :cond_0

    .line 101
    invoke-virtual {v6}, Landroid/content/Intent;->getExtras()Landroid/os/Bundle;

    move-result-object v15

    const-string v16, "package"

    invoke-virtual/range {v15 .. v16}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v15

    if-eqz v15, :cond_0

    .line 102
    invoke-virtual {v6}, Landroid/content/Intent;->getExtras()Landroid/os/Bundle;

    move-result-object v15

    const-string v16, "package"

    invoke-virtual/range {v15 .. v16}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    .line 106
    :cond_0
    new-instance v4, Ljava/util/HashSet;

    invoke-direct {v4}, Ljava/util/HashSet;-><init>()V

    .line 107
    .local v4, "callerPermissionsSet":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/String;>;"
    new-instance v1, Ljava/util/HashSet;

    invoke-direct {v1}, Ljava/util/HashSet;-><init>()V

    .line 108
    .local v1, "calleePermissionSet":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/String;>;"
    invoke-virtual/range {p0 .. p0}, Landroid/app/Activity;->getPackageName()Ljava/lang/String;

    move-result-object v14

    .line 109
    .local v14, "selfPackage":Ljava/lang/String;
    if-eqz v2, :cond_5

    invoke-virtual {v2, v14}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v15

    if-nez v15, :cond_5

    .line 110
    invoke-static {v14, v2}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 112
    const/4 v12, 0x0

    .line 113
    .local v12, "packageInfo":Landroid/content/pm/PackageInfo;
    :try_start_0
    invoke-virtual/range {p0 .. p0}, Landroid/app/Activity;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v15

    invoke-virtual/range {p0 .. p0}, Landroid/app/Activity;->getPackageManager()Landroid/content/pm/PackageManager;

    const/16 v16, 0x1000

    move/from16 v0, v16

    invoke-virtual {v15, v2, v0}, Landroid/content/pm/PackageManager;->getPackageInfo(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;

    move-result-object v12

    .line 114
    iget-object v3, v12, Landroid/content/pm/PackageInfo;->requestedPermissions:[Ljava/lang/String;

    .line 115
    .local v3, "callerPermissions":[Ljava/lang/String;
    if-eqz v3, :cond_1

    .line 116
    invoke-static {v4, v3}, Ljava/util/Collections;->addAll(Ljava/util/Collection;[Ljava/lang/Object;)Z

    .line 118
    :cond_1
    iget-object v13, v12, Landroid/content/pm/PackageInfo;->packageName:Ljava/lang/String;

    .line 119
    .local v13, "packageName":Ljava/lang/String;
    invoke-virtual/range {p0 .. p0}, Landroid/app/Activity;->getComponentName()Landroid/content/ComponentName;

    move-result-object v15

    invoke-virtual {v15}, Landroid/content/ComponentName;->getClassName()Ljava/lang/String;

    move-result-object v5

    .line 120
    .local v5, "className":Ljava/lang/String;
    new-instance v15, Ljava/lang/Throwable;

    invoke-direct {v15}, Ljava/lang/Throwable;-><init>()V

    invoke-virtual {v15}, Ljava/lang/Throwable;->getStackTrace()[Ljava/lang/StackTraceElement;

    move-result-object v15

    const/16 v16, 0x1

    aget-object v15, v15, v16

    invoke-virtual {v15}, Ljava/lang/StackTraceElement;->getMethodName()Ljava/lang/String;

    move-result-object v11

    .line 122
    .local v11, "methodName":Ljava/lang/String;
    invoke-virtual/range {p0 .. p0}, Landroid/app/Activity;->getResources()Landroid/content/res/Resources;

    move-result-object v15

    const/high16 v16, 0x7fff0000

    invoke-virtual/range {v15 .. v16}, Landroid/content/res/Resources;->openRawResource(I)Ljava/io/InputStream;

    move-result-object v8

    .line 123
    .local v8, "is":Ljava/io/InputStream;
    invoke-static {v8}, Lorg/cert/sendsms/CheckActivityRule;->generateEntryPointPermissionMap(Ljava/io/InputStream;)Ljava/util/Map;

    move-result-object v10

    .line 124
    .local v10, "map":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Ljava/util/Set<Ljava/lang/String;>;>;"
    invoke-interface {v10}, Ljava/util/Map;->keySet()Ljava/util/Set;

    move-result-object v15

    invoke-interface {v15}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v16

    :cond_2
    :goto_1
    invoke-interface/range {v16 .. v16}, Ljava/util/Iterator;->hasNext()Z

    move-result v15

    if-eqz v15, :cond_4

    invoke-interface/range {v16 .. v16}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v9

    check-cast v9, Ljava/lang/String;

    .line 126
    .local v9, "key":Ljava/lang/String;
    invoke-virtual {v9, v5}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v15

    if-eqz v15, :cond_2

    invoke-virtual {v9, v11}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v15

    if-eqz v15, :cond_2

    .line 127
    invoke-interface {v10, v9}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    .line 128
    invoke-interface {v10, v9}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v15

    move-object v0, v15

    check-cast v0, Ljava/util/Set;

    move-object v1, v0
    :try_end_0
    .catch Landroid/content/pm/PackageManager$NameNotFoundException; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_1

    .line 98
    .end local v1    # "calleePermissionSet":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/String;>;"
    .end local v3    # "callerPermissions":[Ljava/lang/String;
    .end local v4    # "callerPermissionsSet":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/String;>;"
    .end local v5    # "className":Ljava/lang/String;
    .end local v8    # "is":Ljava/io/InputStream;
    .end local v9    # "key":Ljava/lang/String;
    .end local v10    # "map":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Ljava/util/Set<Ljava/lang/String;>;>;"
    .end local v11    # "methodName":Ljava/lang/String;
    .end local v12    # "packageInfo":Landroid/content/pm/PackageInfo;
    .end local v13    # "packageName":Ljava/lang/String;
    .end local v14    # "selfPackage":Ljava/lang/String;
    :cond_3
    const-string v15, "package"

    invoke-virtual {v6, v15}, Landroid/content/Intent;->getStringExtra(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v15

    goto/16 :goto_0

    .line 131
    .restart local v1    # "calleePermissionSet":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/String;>;"
    .restart local v3    # "callerPermissions":[Ljava/lang/String;
    .restart local v4    # "callerPermissionsSet":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/String;>;"
    .restart local v5    # "className":Ljava/lang/String;
    .restart local v8    # "is":Ljava/io/InputStream;
    .restart local v10    # "map":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Ljava/util/Set<Ljava/lang/String;>;>;"
    .restart local v11    # "methodName":Ljava/lang/String;
    .restart local v12    # "packageInfo":Landroid/content/pm/PackageInfo;
    .restart local v13    # "packageName":Ljava/lang/String;
    .restart local v14    # "selfPackage":Ljava/lang/String;
    :cond_4
    :try_start_1
    invoke-interface {v1, v4}, Ljava/util/Set;->removeAll(Ljava/util/Collection;)Z

    .line 132
    invoke-interface {v1}, Ljava/util/Set;->size()I
    :try_end_1
    .catch Landroid/content/pm/PackageManager$NameNotFoundException; {:try_start_1 .. :try_end_1} :catch_0

    move-result v15

    if-lez v15, :cond_5

    .line 133
    const/4 v15, 0x1

    .line 139
    .end local v3    # "callerPermissions":[Ljava/lang/String;
    .end local v5    # "className":Ljava/lang/String;
    .end local v8    # "is":Ljava/io/InputStream;
    .end local v10    # "map":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Ljava/util/Set<Ljava/lang/String;>;>;"
    .end local v11    # "methodName":Ljava/lang/String;
    .end local v12    # "packageInfo":Landroid/content/pm/PackageInfo;
    .end local v13    # "packageName":Ljava/lang/String;
    :goto_2
    return v15

    .line 135
    .restart local v12    # "packageInfo":Landroid/content/pm/PackageInfo;
    :catch_0
    move-exception v7

    .line 136
    .local v7, "e":Landroid/content/pm/PackageManager$NameNotFoundException;
    invoke-virtual {v7}, Landroid/content/pm/PackageManager$NameNotFoundException;->printStackTrace()V

    .line 139
    .end local v7    # "e":Landroid/content/pm/PackageManager$NameNotFoundException;
    .end local v12    # "packageInfo":Landroid/content/pm/PackageInfo;
    :cond_5
    const/4 v15, 0x0

    goto :goto_2
.end method
