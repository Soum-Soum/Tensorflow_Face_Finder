import getopt
import cv2
import os
import sys

image_folder = './input_media'
output_video_folder = './out/video/'
output_image_folder = './out/images'
video_name = 'test.mp4'
enable_corection = False
black = [0, 0, 0]


def getVideoSize():
    max_height = max_width = 0
    images = [img for img in os.listdir(image_folder) if img.endswith(".jpg")]
    for image in images:
        frame = cv2.imread(os.path.join(image_folder, image))
        height, width, layers = frame.shape
        if height > max_height:
            max_height = height
        if width > max_width:
            max_width = width
    print('Max Height : ', max_height)
    print('Max Width : ', max_width)
    return [max_height, max_width]


def resizeImg(max_height, max_width):
    images = [img for img in os.listdir(image_folder) if img.endswith(".jpg")]
    for image in images:
        img_name = image.__str__()
        frame = cv2.imread(os.path.join(image_folder, image))
        img_height, img_width, img_channels = frame.shape
        if enable_corection:
            frame = histogramCorrection(frame)
        new_frame = cv2.copyMakeBorder(frame, int((max_height - img_height) / 2), int((max_height - img_height) / 2),
                                       int((max_width - img_width) / 2), int((max_width - img_width) / 2),
                                       cv2.BORDER_CONSTANT, value=black)
        cv2.imwrite(os.path.join(output_image_folder, 'modified_' + img_name), new_frame)
        print('Image : ', img_name, ' is computed')


def normalizeImg():
    size = getVideoSize()
    resizeImg(size[0], size[1]);


def histogramCorrection(frame):
    img_y_cr_cb = cv2.cvtColor(frame, cv2.COLOR_BGR2YCrCb)
    y, cr, cb = cv2.split(img_y_cr_cb)
    y_eq = cv2.equalizeHist(y)
    img_y_cr_cb_eq = cv2.merge((y_eq, cr, cb))
    img_rgb_eq = cv2.cvtColor(img_y_cr_cb_eq, cv2.COLOR_YCR_CB2BGR)
    return img_rgb_eq


def image2Video():
    images = [img for img in os.listdir(output_image_folder) if img.endswith(".jpg")]
    frame = cv2.imread(os.path.join(output_image_folder, images[0]))
    height, width, layers = frame.shape
    fourcc = cv2.VideoWriter_fourcc(*'H264')
    video = cv2.VideoWriter(os.path.join(output_video_folder, video_name), fourcc, 24, (width, height))
    for image in images:
        video.write(cv2.imread(os.path.join(output_image_folder, image)))
    video.release()
    cv2.destroyAllWindows()
    return


def main(argv):
    global image_folder, output_video_folder, output_image_folder, enable_corection
    try:
        opts, args = getopt.getopt(argv, "hi:o:c"
                                         "", ["ifile=", "ofile="])
    except getopt.GetoptError:
        print('image2Video.py -i <inputfile> -o <outputfile>')
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print('test.py -i <inputfile> -o <outputfile>')
            sys.exit()
        elif opt in ("-i", "--ifile"):
            image_folder = arg
        elif opt in ("-o", "--ofile"):
            output_video_folder = arg
            output_image_folder = output_video_folder + 'images/'
        elif opt == "-c":
            enable_corection = True
    print('Input file is "', image_folder)
    print('Output file is "', output_video_folder)
    return


main(sys.argv[1:])
normalizeImg()
image2Video()

