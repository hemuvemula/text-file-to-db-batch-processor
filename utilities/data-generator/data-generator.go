package main

import (
	"fmt"
	"math/rand"
	"os"
	"time"
)

// ITAGFileHeader defines the structure for the header in the ITAG file
type ITAGFileHeader struct {
	FileType     string
	FromAgencyID string
	FileDate     string
	FileTime     string
	RecordCount  int
	CountStat1   int
	CountStat2   int
	CountStat3   int
	CountStat4   int
}

// ITAGFileData defines the structure for each detail record in the ITAG file
type ITAGFileData struct {
	TagAgencyID     string
	TagSerialNumber string
	TagStatus       string
	TagAcctInfo     string
}

// Generates a random detail record following the specific rules
func generateDetailRecord() ITAGFileData {
	tagStatusOptions := []string{"1", "2", "3"} // Valid tag statuses
	return ITAGFileData{
		TagAgencyID:     fmt.Sprintf("%03d", rand.Intn(127)),        // 000-127
		TagSerialNumber: fmt.Sprintf("%08d", rand.Intn(16777214)+1), // 00000001-16777215
		TagStatus:       tagStatusOptions[rand.Intn(len(tagStatusOptions))],
		TagAcctInfo:     fmt.Sprintf("%06X", rand.Intn(1<<24)), // Hex-ASCII 0-FFFFFF
	}
}

// Generates the header for the file, assumes it's being called after details to get counts
func generateHeader(recordCount, stat1, stat2, stat3, stat4 int) ITAGFileHeader {
	return ITAGFileHeader{
		FileType:     "ITAG",
		FromAgencyID: fmt.Sprintf("%03d", rand.Intn(1000)),
		FileDate:     time.Now().Format("20060102"),
		FileTime:     time.Now().Format("150405"),
		RecordCount:  recordCount,
		CountStat1:   stat1,
		CountStat2:   stat2,
		CountStat3:   stat3,
		CountStat4:   stat4,
	}
}

// Main function to write data to a file
func main() {
	file, err := os.Create("008_20240414232106.ITAG")
	if err != nil {
		fmt.Println("Error creating file:", err)
		return
	}
	defer file.Close()

	// Generate a set of detail records
	detailCount := 100000
	statCounts := make(map[string]int)
	details := make([]ITAGFileData, detailCount)
	for i := range details {
		detail := generateDetailRecord()
		details[i] = detail
		statCounts[detail.TagStatus]++
	}

	// Generate header based on the number of detail records
	header := generateHeader(detailCount, statCounts["1"], statCounts["2"], statCounts["3"], statCounts["4"])

	// Write header to file
	headerFormat := "%s%s%s%s%08d%08d%08d%08d%08d\n"
	fmt.Fprintf(file, headerFormat,
		header.FileType, header.FromAgencyID, header.FileDate, header.FileTime,
		header.RecordCount, header.CountStat1, header.CountStat2, header.CountStat3, header.CountStat4)

	// Write detail records to file
	detailFormat := "%s%s%s%s\n"
	for _, d := range details {
		fmt.Fprintf(file, detailFormat,
			d.TagAgencyID, d.TagSerialNumber, d.TagStatus, d.TagAcctInfo)
	}
}
