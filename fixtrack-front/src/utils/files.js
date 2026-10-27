// La pestaña se abre antes de pedir el PDF, en el mismo clic, para que el navegador no la bloquee
export async function openPdf(loadBlob) {
  const tab = window.open('', '_blank')
  try {
    const blob = await loadBlob()
    const url = URL.createObjectURL(blob)
    if (tab) {
      tab.location.href = url
    } else {
      window.location.assign(url)
    }
    setTimeout(() => URL.revokeObjectURL(url), 60_000)
  } catch (error) {
    tab?.close()
    throw error
  }
}

export function downloadFile(blob, fileName) {
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  document.body.append(link)
  link.click()
  link.remove()
  setTimeout(() => URL.revokeObjectURL(url), 10_000)
}
