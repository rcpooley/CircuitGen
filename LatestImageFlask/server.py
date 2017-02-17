from flask import Flask, request, send_from_directory, send_file
app = Flask(__name__, static_url_path='')


@app.route("/")
def hello():
    root_dir = app.root_path
    print(request.url)
    return send_from_directory(root_dir, 'showimage.html')


@app.route('/image.jpg')
def get_image():
    return send_file('../image.jpg', mimetype='image/jpg')


if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=5001)
