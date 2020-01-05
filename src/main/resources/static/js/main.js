function byteFormat(limit) {
    var size = (limit / 1024).toFixed(2) + "KB";
    var sizeStr = size + "";
    var index = sizeStr.indexOf(".");
    var dou = sizeStr.substr(index + 1, 2);
    if (dou === "00") {
        return sizeStr.substring(0, index) + sizeStr.substr(index + 3, 2)
    }
    return size;
}

function copyUrls(type) {
    var urls = '';
    for (var index in uploadFiles) {
        var imageUrl = picUrl + uploadFiles[index]['imgUrl'];
        if (type === 1) {
            urls = urls + '<img src="' + imageUrl + '" />' + '\n';
        } else if (type === 2) {
            urls = urls + '[img]' + imageUrl + '[/img]' + '\n';
        } else if (type === 3) {
            urls = urls + '![图片说明](' + imageUrl + ')' + '\n';
        }
    }
    return urls;
}

function onCopySuccess(e) {
    alert('复制成功');
}


