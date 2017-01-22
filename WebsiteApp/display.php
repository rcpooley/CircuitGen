<?php
session_start();
if (!isset($_SESSION['json']))
{
    header('Location: index.html');
}
?>
<html>
<head>
    <title>Display</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0"/>
    <script src="jquery-3.1.1.min.js"></script>
    <style>
        #truths {
            border-collapse: collapse;
            text-align: center;
        }

        #truths td {
            padding: 0 6px;
            border-left: 1px solid gray;
            border-right: 1px solid gray;
        }

        #truths tr {
            border-top: 1px solid gray;
            border-bottom: 1px solid gray;
        }

        #menubar {
            width: 100%;
        }

        #menubar div {
            width: 100%;
            height: 100%;
            background: blue;
            height: 50px;
            text-align: center;
            font-size: 20px;
            color: white;
            display: table;
        }

        .tcell {
            display: table-cell;
            vertical-align: middle;
        }
    </style>
</head>
<body>
<table id="menubar">
    <tr>
        <td>
            <div class="mybutton"><span class="tcell">Truth Table</span></div>
        </td>
        <td>
            <div class="mybutton">Toggle</div>
        </td>
    </tr>
</table>
<table id="truths"></table>
<script>
    var json = "";
    <?php
    echo 'json = \'' . $_SESSION['json'] . '\'' . PHP_EOL;
    ?>
    var obj = JSON.parse(json);
    if (obj.success)
    {
        var tbl = $('#truths')[0];
        var truths = obj['table'];
        var numInputs = obj['numInputs'];
        var head = tbl.insertRow(-1);
        for (var i = 0; i < truths.length; i++)
        {
            if (i < numInputs)
            {
                var cell = head.insertCell(-1);
                cell.innerHTML = 'Input ' + (i + 1);
                if (i == numInputs - 1)
                {
                    $(cell).css('border-right', '3px solid black');
                }
            }
            else
            {
                head.insertCell(-1).innerHTML = 'Output ' + (i - numInputs + 1);
            }
        }
        for (var y = 0; y < truths[0].length; y++)
        {
            var row = tbl.insertRow(-1);
            for (var x = 0; x < truths.length; x++)
            {
                var txt;
                if (truths[x][y])
                    txt = '<span style="color:green">TRUE</span>';
                else
                    txt = '<span style="color:red">FALSE</span>';
                var cell = row.insertCell(-1);
                cell.innerHTML = txt;
                if (x == numInputs - 1)
                {
                    $(cell).css('border-right', '3px solid black');
                }
            }
        }
    }
</script>

</body>
</html>
