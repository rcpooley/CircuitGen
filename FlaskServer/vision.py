import cv2
import numpy as np
import math
import json

LOWER_THRESH = np.array([0, 0, 0], dtype=np.uint8)
UPPER_THRESH = np.array([180, 255, 100], dtype=np.uint8)

CONVEX_THRESHOLD = 0.92

def poly(c, alpha):
    return cv2.approxPolyDP(c, alpha*cv2.arcLength(c, True), True)

def convexRatio(c):
    return cv2.contourArea(c)/cv2.contourArea(cv2.convexHull(c))

def circleRatio(c):
    return cv2.contourArea(c)/(pow(cv2.minEnclosingCircle(c)[1], 2) * math.pi)

def inflateRect(rect):
    return (rect[0], (rect[1][0] * 1.4, rect[1][1] * 1.4), rect[2])

def dist(pt1, pt2):
    return math.sqrt(pow(pt1[0] - pt2[0], 2) + pow(pt1[1] - pt2[1], 2))

class Component:

    HB_RAD_OUT = 30
    HB_RAD_IN = 15

    def __init__(self, cont, typeId):
        self.cont = cont
        self.outHitBoxCenter = (-1, -1)
        self.inHitBoxCenters = []
        self.typeId = typeId

    def checkWires(self, id, wires):
        for i, inHB in enumerate(self.inHitBoxCenters):
            for wire in wires:
                if self.checkConnectedIn(wire.cont, i):
                    wire.toIds.append((id, i))
                    break

        for wire in wires:
            if not wire.hasFromId:
                 if self.checkConnectedOut(wire.cont):
                     wire.setFromId(id)
                     break


    def checkConnectedOut(self, wire):
        for pt in wire:
            if self.withinOut(pt[0]):
                return True
        return False

    def checkConnectedIn(self, wire, i):
        for pt in wire:
            if self.withinIn(pt[0], i):
                return True
        return False

    def withinOut(self, pt):
        return dist(pt, self.outHitBoxCenter) < self.HB_RAD_OUT

    def withinIn(self, pt, i):
        return dist(pt, self.inHitBoxCenters[i]) < self.HB_RAD_IN

class NOT(Component):

    TYPE_ID = 2
    COLORS = (255, 255, 0)

    def __init__(self, cont):
        Component.__init__(self, cont, self.TYPE_ID)
        x, y, w, h = cv2.boundingRect(cont)
        self.rect = inflateRect(((x + w/2,y + h/2), (w,h), 0))

        self.outHitBoxCenter = (int(self.rect[0][0] + self.rect[1][0]/2), int(self.rect[0][1]))
        self.inHitBoxCenters = [(int(self.rect[0][0] - self.rect[1][0]/2), int(self.rect[0][1]))]

    def draw(self, frame, mask):
        box = cv2.boxPoints(self.rect)
        box = np.int0(box)

        cv2.drawContours(mask, [box], 0, (0,0,0), -1)

        cv2.drawContours(frame, [self.cont], 0, self.COLORS, 2)
        cv2.drawContours(frame, [box], 0, self.COLORS, 2)

        cv2.circle(frame, self.outHitBoxCenter, self.HB_RAD_OUT, self.COLORS, 2)
        cv2.circle(frame, self.inHitBoxCenters[0], self.HB_RAD_IN, self.COLORS, 2)

class AND(Component):

    TYPE_ID = 0
    COLORS = (0, 255, 255)

    def __init__(self, cont):
        Component.__init__(self, cont, self.TYPE_ID)
        self.rect = inflateRect(cv2.minAreaRect(cont))

        self.outHitBoxCenter = (int(self.rect[0][0] + self.rect[1][0]/2), int(self.rect[0][1]))
        self.inHitBoxCenters = [(int(self.rect[0][0] - self.rect[1][0]/2), int(self.rect[0][1] - self.rect[1][1]/5)),
                                (int(self.rect[0][0] - self.rect[1][0]/2), int(self.rect[0][1] + self.rect[1][1]/5))]


    def draw(self, frame, mask):
        box = cv2.boxPoints(self.rect)
        box = np.int0(box)

        cv2.drawContours(mask, [box], 0, (0,0,0), -1)

        cv2.drawContours(frame, [self.cont], 0, self.COLORS, 2)
        cv2.drawContours(frame, [box], 0, self.COLORS, 2)

        cv2.circle(frame, self.outHitBoxCenter, self.HB_RAD_OUT, self.COLORS, 2)
        for pt in self.inHitBoxCenters:
            cv2.circle(frame, pt, self.HB_RAD_IN, self.COLORS, 2)

class OR(Component):

    TYPE_ID = 1
    COLORS = (255, 0, 255)

    def __init__(self, cont):
        Component.__init__(self, cont, self.TYPE_ID)
        self.rect = inflateRect(cv2.minAreaRect(cont))

        self.outHitBoxCenter = (int(self.rect[0][0] + self.rect[1][0]/2), int(self.rect[0][1]))
        self.inHitBoxCenters = [(int(self.rect[0][0] - self.rect[1][0]/2), int(self.rect[0][1] - self.rect[1][1]/5)),
                                (int(self.rect[0][0] - self.rect[1][0]/2), int(self.rect[0][1] + self.rect[1][1]/5))]

    def draw(self, frame, mask):
        box = cv2.boxPoints(self.rect)
        box = np.int0(box)

        cv2.drawContours(mask, [box], 0, (0,0,0), -1)

        cv2.drawContours(frame, [self.cont], 0, self.COLORS, 2)
        cv2.drawContours(frame, [box], 0, self.COLORS, 2)

        cv2.circle(frame, self.outHitBoxCenter, self.HB_RAD_OUT, self.COLORS, 2)
        for pt in self.inHitBoxCenters:
            cv2.circle(frame, pt, self.HB_RAD_IN, self.COLORS, 2)

class Wire():

    def __init__(self, cont):
        self.cont = cont
        self.hasFromId = False
        self.fromId = -1
        self.toIds = []

    def setFromId(self, id):
        self.fromId = id
        self.hasFromId = True


def analyzeCircuit(image):

    nparr = np.fromstring(image.read(), np.uint8)
    frame = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

    print frame.shape
    totalArea = frame.shape[0] * frame.shape[1]
    minArea = totalArea / 1690
    Component.HB_RAD_IN = int(math.sqrt(totalArea / (576 * math.pi)) * math.sqrt(2))
    Component.HB_RAD_OUT = int(Component.HB_RAD_IN)

    #frame = cv2.GaussianBlur(frame, (5, 5), 0)

    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
    mask = cv2.inRange(hsv, LOWER_THRESH, UPPER_THRESH)
    mask = cv2.dilate(mask, np.ones((5,5),np.uint8), iterations=1)
    mask = cv2.rectangle(mask, (0, 0), (mask.shape[1] - 1, mask.shape[0] - 1), 0, 1)

    # blur = cv2.GaussianBlur(mask, (0, 0), 3)
    # mask = cv2.addWeighted(mask, 1.5, blur, -0.5, 0)

    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    img = cv2.bitwise_and(frame, frame, mask=mask)

    # edges = cv2.Canny(img, 150, 200)

    _, contours, hierarchy = cv2.findContours(mask.copy(), cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
    contours = [a for i, a in enumerate(contours) if (hierarchy[0][i][2] == -1 and hierarchy[0][i][3] != -1 and cv2.contourArea(a) > minArea)]
    convexes = [a for a in contours if convexRatio(a) > CONVEX_THRESHOLD]
    nonconvexes = [a for a in contours if convexRatio(a) <= CONVEX_THRESHOLD]

    polys = [poly(cont, 0.04) for cont in contours]

    nots = [NOT(a) for a in convexes if len(poly(a, 0.03)) == 3]
    ands = [AND(a) for a in convexes if (len(poly(a, 0.04)) in [4, 5] and circleRatio(a) < 0.75 and circleRatio(a) > 0.5)]
    ors = [OR(a) for a in nonconvexes if (len(poly(a, 0.04)) in [3, 4, 5] and circleRatio(a) > 0.4)]

    if (len(contours)):
        # cv2.drawContours(frame, contours, -1, (255, 0, 0), 1)
        # cv2.drawContours(frame, polys, -1, (0, 255, 0), 1)
        # cv2.drawContours(frame, nonconvexes, -1, (0, 0, 255), 1)
        pass

    components = []
    components.extend(nots)
    components.extend(ands)
    components.extend(ors)

    for comp in components:
        comp.draw(frame, mask)

    _, wireContours, hierarchy = cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    wireContours = [a for a in wireContours if cv2.contourArea(a) > minArea / 4]
    wireContours = [poly(cont, 0.001) for cont in wireContours]
    cv2.drawContours(frame, wireContours, -1, (255,255,255), 2)

    wires = [Wire(w) for w in wireContours]

    for id, comp in enumerate(components):
        comp.checkWires(id, wires)

    sendData = {
        "components": [comp.typeId for comp in components],
        "connections": []
    }

    if (len(components)):
        for i, wire in enumerate(wires):
            if (len(wire.toIds)):
                for toId, toNode in wire.toIds:
                    sendData["connections"].append({
                        "fromid": wire.fromId if wire.fromId >=0 else -i - 1,
                        "toid": toId,
                        "tonode": toNode
                    })
            else:
                cv2.drawContours(frame, [wire.cont], 0, (0, 0, 255), 2)
                if wire.fromId < 0:
                    print "ERROR: wire has {-1, -1, -1}"
                sendData["connections"].append({
                    "fromid": wire.fromId,
                    "toid": -1,
                    "tonode": -1
                })

    print 'saved image to "image.jpg": ' + str(cv2.imwrite('image.jpg', frame))
    cv2.imwrite('mask.jpg', mask)

    print sendData
    return json.dumps(sendData)

