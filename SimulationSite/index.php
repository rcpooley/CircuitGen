<?php
require('connect.php');
?>
    <html>
    <head>
        <title>Circuit Simulator</title>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>
        <style>
            #mycanvas {
                position: absolute;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
            }
        </style>
    </head>
    <body>
    <canvas id="mycanvas"></canvas>
    <script>
        var canvas = $('#mycanvas')[0];

        function updateCanvas()
        {
            canvas.width = $(window).width();
            canvas.height =$(window).height();
        }
        $(window).resize(updateCanvas);
        updateCanvas();

        var ctx = canvas.getContext('2d');

        var pixelRatio = 10;

        //Load images
        var images = {};
        var toload = ["and", "or", "not", "relay"];
        for (var s in toload)
        {
            var img = new Image();
            img.src = "res/" + toload[s] + ".png";
            images[toload[s]] = img;
        }

        function Value()
        {
            this.value = false;
        }

        function Component(type, x, y, w, h)
        {
            this.type = type;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.rotation = 0;
            this.inputs = [new Value()];
            if (type == "and" || type == "or")
            {
                this.inputs.push(new Value());
            }
            this.outputs = [new Value()];
            this.getAngle = function() {
                if (this.rotation == 0) return 0;
                else if (this.rotation == 1) return Math.PI / 2;
                else if (this.rotation == 2) return Math.PI;
                else return Math.PI * 3 / 2;
            };
            this.getDots = function() {
                var dots = [];

                var ni = this.inputs.length;
                if (rotation % 2 == 0)
                {
                    var x = rotation == 0 ? 0 : 1;
                    if (ni == 2)
                    {
                        dots.push({x: x, y: 0.25});
                        dots.push({x: x, y: 0.75});
                    }
                    else
                    {
                        dots.push({x: x, y: 0.5});
                    }
                    dots.push({x: 1 - x, y: 0.5});
                }
                else
                {
                    var y = rotation == 1 ? 0 : 1;
                    if (ni == 2)
                    {
                        dots.push({x: 0.25, y: y});
                        dots.push({x: 0.75, y: y});
                    }
                    else
                    {
                        dots.push({x: 0.5, y: y});
                    }
                    dots.push({x: 0.5, y: 1 - y});
                }

                return dots;
            };
        }

        var mse = {x: 0, y: 0};
        var components = [];
        var tempComponent, hoverComponent;
        var wiringMode = false;

        function updateTempComponent(type)
        {
            if (typeof tempComponent == "undefined")
            {
                tempComponent = new Component(type, mse.x / pixelRatio, mse.y / pixelRatio, 4, 4);
            }
            else
            {
                tempComponent.type = type;
            }
        }

        function draw()
        {
            ctx.fillStyle = "#FFFFFF";
            ctx.fillRect(0, 0, canvas.width, canvas.height);

            //Update tempcomponent location
            if (typeof tempComponent != "undefined")
            {
                tempComponent.x = parseInt(mse.x / pixelRatio - tempComponent.w / 2);
                tempComponent.y = parseInt(mse.y / pixelRatio - tempComponent.h / 2);
            }

            //Draw grid dots
            ctx.fillStyle = "#000000";
            for (var x = 0; x < canvas.width; x += pixelRatio)
            {
                for (var y = 0; y < canvas.height; y += pixelRatio)
                {
                    ctx.fillRect(x, y, 1, 1)
                }
            }

            //draw components
            var todraw = components;
            if (typeof tempComponent != "undefined")
                todraw = todraw.concat(tempComponent);
            var hoverComp;
            for (var ind in todraw)
            {
                var c = todraw[ind];
                var img = images[c.type];
                var xx = c.x * pixelRatio;
                var yy = c.y * pixelRatio;
                var wid = c.w * pixelRatio;
                var hei = c.h * pixelRatio;
                ctx.save();
                ctx.translate(xx + wid / 2, yy + wid / 2)
                ctx.rotate(c.getAngle());
                ctx.drawImage(img, -wid / 2, -hei / 2, wid, hei);
                ctx.restore();

                if (!wiringMode && c != tempComponent && mse.x >= xx && mse.y >= yy && mse.x < xx + wid && mse.y < yy + hei)
                {
                    hoverComp = c;
                    ctx.fillStyle = "rgba(0, 0, 255, 0.5)";
                    ctx.fillRect(xx, yy, wid, hei);
                }
            }
            hoverComponent = hoverComp;

            //draw wire nodes
            if (wiringMode)
            {

            }
        }
        setInterval(draw, 1000 / 60);

        var types = {
            "1": "and",
            "2": "or",
            "3": "not",
            "4": "relay"
        };

        //Key listener
        $('body').keydown(function(e) {
            var c = String.fromCharCode(e.keyCode);
            if (!wiringMode)
            {
                if (c in types)
                {
                    updateTempComponent(types[c]);
                }
            }

            if (c == "W")
            {
                if (typeof tempComponent == "undefined")
                {
                    wiringMode = !wiringMode;
                }
            }
        });

        $(canvas).mousemove(function (e) {
            mse.x = e.clientX;
            mse.y = e.clientY;
        });

        $(canvas).mouseup(function (e) {
            if (e.button == 0)
            {
                if (!wiringMode)
                {
                    if (typeof tempComponent != "undefined")
                    {
                        components.push(tempComponent);
                        tempComponent = undefined;
                    }
                    else if (typeof hoverComponent != "undefined")
                    {
                        components.splice(components.indexOf(hoverComponent), 1);
                        tempComponent = hoverComponent;
                    }
                }
            }
            else if (e.button == 2)
            {
                if (typeof tempComponent != "undefined")
                {
                    var rot = tempComponent.rotation + 1;
                    if (rot >= 4) rot = 0;
                    tempComponent.rotation = rot;
                }
            }
        });

        $('body').on('contextmenu', function(e){ return false; });
    </script>
    </body>
    </html>
<?php
$conn->close();
?>