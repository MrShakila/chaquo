#!/usr/bin/env python3
#---------------------------------------------------

import numpy as np  # Module that simplifies computations on matrices
from pylsl import StreamInlet, resolve_byprop  # Module to receive EEG data



# Handy little enum to make code more readable


def main():

        """ EXPERIMENTAL PARAMETERS """
        # Modify these to change aspects of the signal processing

        # Length of the EEG data buffer (in seconds)
        # This buffer will hold last n seconds of data and be used for calculations
        BUFFER_LENGTH = 3

        # Length of the epochs used to compute the FFT (in seconds)
        EPOCH_LENGTH = 1

        # Amount of overlap between two consecutive epochs (in seconds)
        OVERLAP_LENGTH = 0.8

        # Amount to 'shift' the start of each next consecutive epoch
        SHIFT_LENGTH = EPOCH_LENGTH - OVERLAP_LENGTH

        #num of blink in the stream
        blink_count = 0
        t = 0

        d = 0.7 #trough distance
        h = -90  #trough height

        # Index of the channel(s) (electrodes) to be used
        # 0 = left ear, 1 = left forehead, 2 = right forehead, 3 = right ear
        #EYE_CHANNEL = [0]
        CON_CHANNEL = [3]

        """ 1. CONNECT TO EEG STREAM """
        # Search for active LSL streams
        print('Looking for an EEG stream...')
        streams = resolve_byprop('type', 'EEG', timeout=2)
        if len(streams) == 0:
            raise RuntimeError('Can\'t find EEG stream.')

        # Set active EEG stream to inlet and apply time correction
        print("Start acquiring data")
        inlet = StreamInlet(streams[0], max_chunklen=12)
        eeg_time_correction = inlet.time_correction()

        # Get the stream info and description
        info = inlet.info()
        description = info.desc()

        # Get the sampling frequency
        # This is an important value that represents how many EEG data points are
        # collected in a second. This influences our frequency band calculation.
        # for the Muse 2016, this should always be 256
        fs = int(info.nominal_srate())

        """ 2. INITIALIZE BUFFERS """

        # Initialize raw concentration EEG data buffer
        con_buffer = np.zeros((int(fs * BUFFER_LENGTH), 1))
        filter_state = None  # for use with the notch filter
        eye_filter_state = None

        eye_buffer = np.zeros((int(fs * BUFFER_LENGTH), 1))

        # Compute the number of epochs in "buffer_length"
        n_win_test = int(np.floor((BUFFER_LENGTH - EPOCH_LENGTH) /
                                SHIFT_LENGTH + 1))

        # Initialize the band power buffer (for plotting)
        # bands will be ordered: [delta, theta, alpha, beta]
        band_buffer = np.zeros((n_win_test, 4))

        #intialize welch PSD
        #self.welch_psd = utils.cal_welch_PSD(fs=self.fs)
        return band_buffer

