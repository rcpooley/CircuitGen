from flask import Flask, request
import socket
#import vision.py
app = Flask(__name__)

xorCircuitJson = '{"components":[2,2,2,2,2,1,1],"connections":[{"toid":0,"fromid":-1,"tonode":0},{"toid":-1,"fromid":0,"tonode":0},{"toid":1,"fromid":-1,"tonode":0},{"toid":2,"fromid":-1,"tonode":0},{"toid":3,"fromid":-1,"tonode":0},{"toid":4,"fromid":-1,"tonode":0},{"toid":5,"fromid":3,"tonode":0},{"toid":5,"fromid":4,"tonode":1},{"toid":-1,"fromid":5,"tonode":0},{"toid":6,"fromid":1,"tonode":0},{"toid":6,"fromid":2,"tonode":1},{"toid":-1,"fromid":6,"tonode":0}]}';


@app.route("/upload", methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        f = request.files['file']
        f.save(f.filename)
        return sendstring(xorCircuitJson)


@app.route("/")
def hello():
    return sendstring(xorCircuitJson)


def sendstring(str):
    sock = socket.socket()
    sock.connect(("localhost", 7276))
    sock.send(str.encode(encoding='UTF-8'))
    sock.settimeout(5)

    ret = ""

    read = 1024
    while read == 1024:
        try:
            n = sock.recv(1024)
            read = len(n)
            ret += n
        except socket.timeout:
            ret = "[timed out]"

    sock.close()
    return ret

if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0')
