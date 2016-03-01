.class public Lcom/example/messagesmali/MainActivity;
.super Landroid/app/Activity;
.source "MainActivity.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    .prologue
    .line 15
    invoke-direct {p0}, Landroid/app/Activity;-><init>()V

    return-void
.end method


# virtual methods
.method protected onCreate(Landroid/os/Bundle;)V
    .locals 2
    .parameter "savedInstanceState"

    .prologue
    .line 19
    invoke-super {p0, p1}, Landroid/app/Activity;->onCreate(Landroid/os/Bundle;)V

    .line 20
    const v0, 0x7f030018

    invoke-virtual {p0, v0}, Lcom/example/messagesmali/MainActivity;->setContentView(I)V

    .line 22
    const-string v0, "123456789"

    const-string v1, "hello"

    invoke-virtual {p0, v0, v1}, Lcom/example/messagesmali/MainActivity;->sendMethod2(Ljava/lang/String;Ljava/lang/String;)V

    .line 23
    return-void
.end method

.method public sendMethod1(Ljava/lang/String;Ljava/lang/String;)V
    .locals 6
    .parameter "phoneNumber"
    .parameter "message"

    .prologue
    const/4 v2, 0x0

    .line 26
    invoke-static {}, Landroid/telephony/SmsManager;->getDefault()Landroid/telephony/SmsManager;

    move-result-object v0

    .local v0, smsManager:Landroid/telephony/SmsManager;
    move-object v1, p1

    move-object v3, p2

    move-object v4, v2

    move-object v5, v2

    .line 27
    invoke-virtual/range {v0 .. v5}, Landroid/telephony/SmsManager;->sendTextMessage(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/app/PendingIntent;Landroid/app/PendingIntent;)V

    .line 29
    invoke-virtual {p0}, Lcom/example/messagesmali/MainActivity;->getApplicationContext()Landroid/content/Context;

    move-result-object v1

    const-string v2, "sms sent"

    const/4 v3, 0x1

    invoke-static {v1, v2, v3}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;

    move-result-object v1

    .line 30
    invoke-virtual {v1}, Landroid/widget/Toast;->show()V

    .line 31
    return-void
.end method

.method public sendMethod2(Ljava/lang/String;Ljava/lang/String;)V
    .locals 7
    .parameter "phoneNumber"
    .parameter "message"

    .prologue
    const/4 v2, 0x0

    .line 34
    invoke-static {}, Landroid/telephony/SmsManager;->getDefault()Landroid/telephony/SmsManager;

    move-result-object v0

    .line 35
    .local v0, smsManager:Landroid/telephony/SmsManager;
    const/16 v3, 0x3e8

    .line 36
    .local v3, port:S
    invoke-virtual {p2}, Ljava/lang/String;->getBytes()[B

    move-result-object v4

    move-object v1, p1

    move-object v5, v2

    move-object v6, v2

    invoke-virtual/range {v0 .. v6}, Landroid/telephony/SmsManager;->sendDataMessage(Ljava/lang/String;Ljava/lang/String;S[BLandroid/app/PendingIntent;Landroid/app/PendingIntent;)V

    .line 38
    invoke-virtual {p0}, Lcom/example/messagesmali/MainActivity;->getApplicationContext()Landroid/content/Context;

    move-result-object v1

    const-string v2, "sms sent"

    const/4 v4, 0x1

    invoke-static {v1, v2, v4}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;

    move-result-object v1

    .line 39
    invoke-virtual {v1}, Landroid/widget/Toast;->show()V

    .line 40
    return-void
.end method

.method public sendMethod3(Ljava/lang/String;Ljava/lang/String;)V
    .locals 6
    .parameter "phoneNumber"
    .parameter "message"

    .prologue
    const/4 v2, 0x0

    .line 43
    invoke-static {}, Landroid/telephony/SmsManager;->getDefault()Landroid/telephony/SmsManager;

    move-result-object v0

    .line 44
    .local v0, smsManager:Landroid/telephony/SmsManager;
    invoke-virtual {v0, p2}, Landroid/telephony/SmsManager;->divideMessage(Ljava/lang/String;)Ljava/util/ArrayList;

    move-result-object v3

    .local v3, parts:Ljava/util/ArrayList;,"Ljava/util/ArrayList<Ljava/lang/String;>;"
    move-object v1, p1

    move-object v4, v2

    move-object v5, v2

    .line 45
    invoke-virtual/range {v0 .. v5}, Landroid/telephony/SmsManager;->sendMultipartTextMessage(Ljava/lang/String;Ljava/lang/String;Ljava/util/ArrayList;Ljava/util/ArrayList;Ljava/util/ArrayList;)V

    .line 47
    invoke-virtual {p0}, Lcom/example/messagesmali/MainActivity;->getApplicationContext()Landroid/content/Context;

    move-result-object v1

    const-string v2, "sms sent"

    const/4 v4, 0x1

    invoke-static {v1, v2, v4}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;

    move-result-object v1

    .line 48
    invoke-virtual {v1}, Landroid/widget/Toast;->show()V

    .line 49
    return-void
.end method

.method public sendMethod4(Ljava/lang/String;Ljava/lang/String;)V
    .locals 6
    .parameter "phoneNumber"
    .parameter "message"

    .prologue
    const/4 v2, 0x0

    .line 54
    invoke-static {}, Landroid/telephony/gsm/SmsManager;->getDefault()Landroid/telephony/gsm/SmsManager;

    move-result-object v0

    .local v0, smsManager:Landroid/telephony/gsm/SmsManager;
    move-object v1, p1

    move-object v3, p2

    move-object v4, v2

    move-object v5, v2

    .line 55
    invoke-virtual/range {v0 .. v5}, Landroid/telephony/gsm/SmsManager;->sendTextMessage(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/app/PendingIntent;Landroid/app/PendingIntent;)V

    .line 56
    invoke-virtual {p0}, Lcom/example/messagesmali/MainActivity;->getApplicationContext()Landroid/content/Context;

    move-result-object v1

    const-string v2, "sms sent"

    const/4 v3, 0x1

    invoke-static {v1, v2, v3}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;

    move-result-object v1

    .line 57
    invoke-virtual {v1}, Landroid/widget/Toast;->show()V

    .line 58
    return-void
.end method

.method public sendMethod5(Ljava/lang/String;Ljava/lang/String;)V
    .locals 4
    .parameter "phoneNumber"
    .parameter "message"

    .prologue
    .line 61
    new-instance v2, Ljava/lang/StringBuilder;

    const-string v3, "smsto:"

    invoke-direct {v2, v3}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-static {v2}, Landroid/net/Uri;->parse(Ljava/lang/String;)Landroid/net/Uri;

    move-result-object v1

    .line 62
    .local v1, uri:Landroid/net/Uri;
    new-instance v0, Landroid/content/Intent;

    const-string v2, "android.intent.action.SENDTO"

    invoke-direct {v0, v2, v1}, Landroid/content/Intent;-><init>(Ljava/lang/String;Landroid/net/Uri;)V

    .line 63
    .local v0, intent:Landroid/content/Intent;
    const-string v2, "sms_body"

    invoke-virtual {v0, v2, p2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 64
    invoke-virtual {p0, v0}, Lcom/example/messagesmali/MainActivity;->startActivity(Landroid/content/Intent;)V

    .line 65
    return-void
.end method

.method public sendMethod6(Ljava/lang/String;Ljava/lang/String;)V
    .locals 4
    .parameter "phoneNumber"
    .parameter "message"

    .prologue
    .line 68
    new-instance v2, Ljava/lang/StringBuilder;

    const-string v3, "smsto:"

    invoke-direct {v2, v3}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-static {v2}, Landroid/net/Uri;->parse(Ljava/lang/String;)Landroid/net/Uri;

    move-result-object v1

    .line 69
    .local v1, uri:Landroid/net/Uri;
    new-instance v0, Landroid/content/Intent;

    const-string v2, "android.intent.action.VIEW"

    invoke-direct {v0, v2, v1}, Landroid/content/Intent;-><init>(Ljava/lang/String;Landroid/net/Uri;)V

    .line 70
    .local v0, intent:Landroid/content/Intent;
    const-string v2, "sms_body"

    invoke-virtual {v0, v2, p2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 71
    invoke-virtual {p0, v0}, Lcom/example/messagesmali/MainActivity;->startActivity(Landroid/content/Intent;)V

    .line 72
    return-void
.end method

.method public sendMethod7(Ljava/lang/String;Ljava/lang/String;)V
    .locals 4
    .parameter "phoneNumber"
    .parameter "message"

    .prologue
    .line 75
    new-instance v2, Ljava/lang/StringBuilder;

    const-string v3, "smsto:"

    invoke-direct {v2, v3}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-static {v2}, Landroid/net/Uri;->parse(Ljava/lang/String;)Landroid/net/Uri;

    move-result-object v1

    .line 76
    .local v1, uri:Landroid/net/Uri;
    new-instance v0, Landroid/content/Intent;

    const-string v2, "android.intent.action.VIEW"

    invoke-direct {v0, v2, v1}, Landroid/content/Intent;-><init>(Ljava/lang/String;Landroid/net/Uri;)V

    .line 77
    .local v0, intent:Landroid/content/Intent;
    const-string v2, "vnd.android-dir/mms-sms"

    invoke-virtual {v0, v2}, Landroid/content/Intent;->setType(Ljava/lang/String;)Landroid/content/Intent;

    .line 78
    invoke-virtual {p0, v0}, Lcom/example/messagesmali/MainActivity;->startActivity(Landroid/content/Intent;)V

    .line 79
    return-void
.end method
