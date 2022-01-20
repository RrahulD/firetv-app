package tv.accedo.dishonstream2.ui.main.tvguide.classicepg

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import hu.accedo.commons.tools.ComponentTools
import hu.accedo.commons.widgets.epg.EpgAttributeHolder
import hu.accedo.commons.widgets.epg.EpgLayoutManager
import hu.accedo.commons.widgets.epg.EpgView
import hu.accedo.commons.widgets.epg.adapter.EpgItem
import tv.accedo.dishonstream2.R

open class FocusHandler(epgView: EpgView) : EpgLayoutManager.FocusHandler(epgView) {

    private var focusedItem: EpgItem? = null
    private var focusedX = 0
    private var row = 0
    private var column = 0
    private var maxColumn = 0
    private var scrollX = 0
    private var scrollY = 0
    private var steppingWidth = 0
    private var drag = false
    private var isRtl = false
    private var firstStart = true
    private val attrs: EpgAttributeHolder
    private val recyclerView: RecyclerView

    init {
        attrs = epgView.attributes
        recyclerView = epgView.recyclerView
    }

    override fun onLayoutChildren(
        totalWidth: Int,
        totalHeight: Int,
        pixelStart: Int,
        pixelEnd: Int
    ) {
        steppingWidth = attrs.timebarMinuteStepping * attrs.minuteWidth
        focusedX = column * steppingWidth + attrs.channelsWidth
        maxColumn = (pixelEnd - recyclerView.width + attrs.channelsWidth) / steppingWidth
        isRtl = ComponentTools.isRtl(recyclerView)
    }

    override fun onUpdateView(
        scrollX: Int,
        scrollY: Int,
        firstVisibleRow: Int,
        lastVisibleRow: Int
    ) {
        this.scrollX = scrollX
        this.scrollY = scrollY

        if (adapter == null || adapter.isEmpty) {
            return
        }
        if (firstStart || drag) {
            row = 0
        }
        if (firstStart || drag) {
            column = Math.round(scrollX.toFloat() / steppingWidth)
            focusedX = scrollX + attrs.channelsWidth
        }
        firstStart = false
        updateFocus()
    }

    override fun onUpdateEpgItem(view: View, epgItem: EpgItem) {
        if (focusedItem === epgItem && !view.hasFocus() && hadFocus()) {
            view.requestFocus()
        }
    }

    override fun onInterceptFocusSearch(focusedView: View, direction: Int): View? {
        val focused = focusedView.getTag(R.id.epg_item) as? EpgItem?
            ?: return null
        if (!isRtl && direction == View.FOCUS_RIGHT || isRtl && direction == View.FOCUS_LEFT) {
            if (focused.rect.right >= (column + 1) * steppingWidth + attrs.channelsWidth && column < maxColumn) {
                scrollByFocus(1, 0, true)
            } else {
                focusedX = focused.rect.right + 1
                updateFocus()
            }
        } else if (direction == View.FOCUS_UP) {
            if (attrs.isLoopingEnabled || row > 0) {
                scrollByFocus(0, -1, true)
            } else {
                onFocusOutUp()
            }
        } else if (direction == View.FOCUS_DOWN) {
            if (attrs.isLoopingEnabled || row < adapter.getChannelCount(true) - 1) {
                scrollByFocus(0, 1, true)
            }
        } else if (!isRtl && direction == View.FOCUS_LEFT || isRtl && direction == View.FOCUS_RIGHT) {
            if (focused.rect.left > scrollX + attrs.channelsWidth || column == 0) {
                focusedX = focused.rect.left - 1
                updateFocus()
            } else {
                if (focused.rect.left <= (column - 1) * steppingWidth + attrs.channelsWidth) {
                    scrollByFocus(-1, 0, false)
                } else if (Math.abs(scrollX / steppingWidth - column) < 2) {
                    focusedX = focused.rect.left - 1
                    scrollByFocus(-1, 0, false)
                }
            }
        }
        val newFocusedView = getView(focusedItem)
        return newFocusedView ?: focusedView
    }

    open fun onFocusOutUp() {}

    override fun onScrollStateChanged(state: Int) {
        if (state == RecyclerView.SCROLL_STATE_DRAGGING) {
            drag = true
        } else if (state == RecyclerView.SCROLL_STATE_IDLE) {
            if (drag) {
                scrollByFocus(0, 0, true)
            }
            drag = false
        }
    }

    override fun requestChildFocus(child: View, focused: View) {
        val focusedView = getView(focusedItem)
        if (focusedView != null && focusedView !== focused) {
            focusedView.requestFocus()
        }
    }

    fun scrollByFocus(x: Int, y: Int, updateFocus: Boolean) {
        row += y
        column += x
        if (updateFocus) {
            focusedX = column * steppingWidth + attrs.channelsWidth
        }
        val topOffset = (recyclerView.height - attrs.timebarHeight - attrs.rowHeight) / 2
        val targetScrollX = column * steppingWidth
        val targetScrollY = row * attrs.rowHeight - topOffset
        val scrollDiffX = targetScrollX - scrollX
        val scrollDiffY = targetScrollY - scrollY
        if (Math.abs(scrollDiffX) > 0 || Math.abs(scrollDiffY) > 0) {
            recyclerView.smoothScrollBy(scrollDiffX, scrollDiffY)
        }
    }

    fun updateFocus() {
        focusedItem =
            adapter.getEpgItemProgramsIterator(row, focusedX, scrollX + recyclerView.width, true)
                .next()
    }
}