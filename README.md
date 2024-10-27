# VoA-Helper

Voa Helper is an open source helper app for Samsung Galaxy A52s that lets the user install and manage windows on his phone

### How to use the install feature
Requirements:
- Unlocked bootloader
- Root
- wim/esd file of windows
- Free space. Not less then 15GB
- Backups because format data will be required
- [All partitions set]()

First step is to decide what windows you want.
There are two options:
- Pre-made Images. They are faster and preconfigurate will all stuff needed
- Custom Images. They are a bit slower to install but you'll need to configure it yourself

So where do you get an pre-made image you may ask.
Its simple. They are here:
- [Windows 11 Pro 22h2 with Atlas OS](https://www.dropbox.com/scl/fi/cc9e3btnzs34bmnlbvpqe/win11_22h2_atlasos_desktop.wim?rlkey=35iuwtqzw4ofrut8d3z2m17w4&e=1&st=e7it86jw&dl=0)

And custom wim/esd are in ISO files in *source* folder.

Now you need to open the [app](https://github.com/VendDair/VoA-Helper/releases) and exit it.

It created 2 folder that are needed for it to function proprierly.

Then [download](https://github.com/VendDair/VoA-Helper/releases) **Driver.zip**, **pe.img** and **uefi.img**

Put **Driver.zip** and **pe.img** in *WindowsInstall* folder

<img src="https://github.com/VendDair/VoA-Helper/blob/main/Guide%20stuff/windowsinstall.png" alt="Image" width="300" />

Then put **uefi.img** in UEFI folder

<img src="https://github.com/VendDair/VoA-Helper/blob/main/Guide%20stuff/uefi.png" alt="Image" width="300" />

After that go into the app and press *Install Windows* and press *yes*

Then select your preffered method, select the wim/esd listed there (If you put another number then listed ones the app will crash) and select the index listed there you want

So thats all. Now go make an tee because when you'll be back Windows will be already installed :)

*If theres any issues please say that [here](https://t.me/a52sxq_uefi) with screenshots/pics and any other detail that can help fixing the problem*

## FAQ
- How do I boot into android back? Flash boot.img from /sdcard/ in twrp or launch "Android" app on the Windows Desktop
- How do I uninstall windows? [here]()
