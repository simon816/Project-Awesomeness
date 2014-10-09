<?php
if (isset($_GET['action'])) {
    if ($_GET['action'] == 'getHostIP') {
        die(file_get_contents('hostip.txt'));
    } elseif ($_GET['action'] == 'updateIP') {
        file_put_contents('hostip.txt', $_POST['ip']);
        exit;
    } elseif ($_GET['action'] == 'download') {
        $file = 'ProjectAwesomeness-1.1.5.19.jar';
        header('Content-Description: File Transfer');
        header('Content-Type: application/octet-stream');
        header("Content-Disposition: attachment; filename=$file");
        header('Content-Transfer-Encoding: binary');
        header('Expires: 0');
        header('Cache-Control: must-revalidate');
        header('Pragma: public');
        header('Content-Length: ' . filesize($file));
        readfile("$file");
        exit;
    }
}
?>
<h3><a href="awesome.php?action=download">Download <b>Project Awesomeness</b></a></h3>